package com.base.androidstartertemplate.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.base.androidstartertemplate.data.local.CompletedWorkoutEntity
import com.base.androidstartertemplate.data.local.PersonalRecordEntity
import com.base.androidstartertemplate.data.local.RoutineEntity
import com.base.androidstartertemplate.data.local.WorkoutDatabase
import com.base.androidstartertemplate.data.model.DefaultExercises
import com.base.androidstartertemplate.data.model.EquipmentType
import com.base.androidstartertemplate.data.model.Exercise
import com.base.androidstartertemplate.utility.AudioHapticHelper
import com.base.androidstartertemplate.utility.VoiceCoachHelper
import com.base.androidstartertemplate.worker.ReminderManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ActiveWorkoutUiState(
    val activeExercise: Exercise? = null,
    val routineName: String = "",
    val routineExercises: List<Exercise> = emptyList(),
    val currentExerciseIndex: Int = 0,
    val isRoutineMode: Boolean = false,
    val isWorkoutActive: Boolean = false,
    val isPaused: Boolean = false,
    val isResting: Boolean = false,
    val isCountingDown: Boolean = false,
    val countdownSeconds: Int = 3,
    val restTimerSeconds: Int = 0,
    val totalRestSeconds: Int = 30,
    val timerSeconds: Int = 0,
    val currentSetIndex: Int = 1,
    val completedReps: Int = 0,
    val currentWeightKg: Double = 0.0,
    val totalVolumeKg: Double = 0.0,
    val caloriesBurned: Int = 0,
    val isWorkoutFinished: Boolean = false,
    val isNewPR: Boolean = false,
    val newPRMessage: String = ""
)

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    init {
        VoiceCoachHelper.init(application)
    }

    private val db = WorkoutDatabase.getDatabase(application)
    private val workoutDao = db.workoutDao()
    private val routineDao = db.routineDao()
    private val prDao = db.personalRecordDao()

    val completedWorkouts: StateFlow<List<CompletedWorkoutEntity>> = workoutDao.getAllCompletedWorkouts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val customRoutines: StateFlow<List<RoutineEntity>> = routineDao.getAllRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val personalRecords: StateFlow<List<PersonalRecordEntity>> = prDao.getAllPRs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _selectedEquipmentSet = MutableStateFlow<Set<EquipmentType>>(emptySet())
    val selectedEquipmentSet: StateFlow<Set<EquipmentType>> = _selectedEquipmentSet.asStateFlow()

    private val _selectedMuscleGroups = MutableStateFlow<Set<String>>(emptySet())
    val selectedMuscleGroups: StateFlow<Set<String>> = _selectedMuscleGroups.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _exercises = MutableStateFlow(DefaultExercises.list)
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    val filteredExercises: StateFlow<List<Exercise>> = combine(
        _exercises,
        _selectedEquipmentSet,
        _selectedMuscleGroups,
        _searchQuery
    ) { list, selectedEquipment, selectedMuscles, query ->
        list.filter { exercise ->
            val matchesEquipment = selectedEquipment.isEmpty() || selectedEquipment.contains(exercise.equipment)
            val matchesMuscle = selectedMuscles.isEmpty() || selectedMuscles.any { m ->
                exercise.targetMuscle.contains(m, ignoreCase = true)
            }
            val matchesQuery = query.isBlank() || exercise.name.contains(query, ignoreCase = true) || exercise.targetMuscle.contains(query, ignoreCase = true)
            matchesEquipment && matchesMuscle && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DefaultExercises.list
    )

    private val _activeWorkoutState = MutableStateFlow(ActiveWorkoutUiState())
    val activeWorkoutState: StateFlow<ActiveWorkoutUiState> = _activeWorkoutState.asStateFlow()

    private val prefs = application.getSharedPreferences(ReminderManager.PREFS_NAME, Context.MODE_PRIVATE)

    private val _isReminderEnabled = MutableStateFlow(prefs.getBoolean(ReminderManager.KEY_ENABLED, false))
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

    private val _reminderHour = MutableStateFlow(prefs.getInt(ReminderManager.KEY_HOUR, 18))
    val reminderHour: StateFlow<Int> = _reminderHour.asStateFlow()

    private val _reminderMinute = MutableStateFlow(prefs.getInt(ReminderManager.KEY_MINUTE, 0))
    val reminderMinute: StateFlow<Int> = _reminderMinute.asStateFlow()

    private val _activeDaysSet = MutableStateFlow<Set<Int>>(
        prefs.getString(ReminderManager.KEY_ACTIVE_DAYS, "1,2,3,4,5,6,7")
            ?.split(",")?.mapNotNull { it.toIntOrNull() }?.toSet() ?: setOf(1, 2, 3, 4, 5, 6, 7)
    )
    val activeDaysSet: StateFlow<Set<Int>> = _activeDaysSet.asStateFlow()

    private val _reminderTheme = MutableStateFlow(prefs.getString(ReminderManager.KEY_THEME, "Beast Mode 💪") ?: "Beast Mode 💪")
    val reminderTheme: StateFlow<String> = _reminderTheme.asStateFlow()

    private val _isVoiceCoachEnabled = MutableStateFlow(prefs.getBoolean("voice_coach_enabled", true))
    val isVoiceCoachEnabled: StateFlow<Boolean> = _isVoiceCoachEnabled.asStateFlow()

    fun toggleVoiceCoach(enabled: Boolean) {
        _isVoiceCoachEnabled.value = enabled
        prefs.edit().putBoolean("voice_coach_enabled", enabled).apply()
        if (enabled) {
            VoiceCoachHelper.speak("Voice Coach enabled", true)
        }
    }

    fun testVoiceCoach() {
        VoiceCoachHelper.speak("Hello! I am your AI Voice Coach. Let's crush this workout session!", true)
    }

    private var workoutTimerJob: Job? = null
    private var restTimerJob: Job? = null
    private var countdownTimerJob: Job? = null

    fun toggleEquipmentFilter(equipment: EquipmentType?) {
        _selectedEquipmentSet.update { current ->
            if (equipment == null) {
                emptySet()
            } else {
                if (current.contains(equipment)) current - equipment else current + equipment
            }
        }
    }

    fun toggleMuscleGroupFilter(muscle: String?) {
        _selectedMuscleGroups.update { current ->
            if (muscle == null) {
                emptySet()
            } else {
                if (current.contains(muscle)) current - muscle else current + muscle
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun startWorkout(exercise: Exercise) {
        stopTimers()
        _activeWorkoutState.update {
            ActiveWorkoutUiState(
                activeExercise = exercise,
                routineName = exercise.name,
                routineExercises = listOf(exercise),
                currentExerciseIndex = 0,
                isRoutineMode = false,
                isWorkoutActive = true,
                isPaused = false,
                isResting = false,
                isCountingDown = true,
                countdownSeconds = 3,
                timerSeconds = 0,
                currentSetIndex = 1,
                completedReps = 0,
                currentWeightKg = if (exercise.equipment == EquipmentType.BODYWEIGHT) 0.0 else 10.0,
                totalVolumeKg = 0.0,
                caloriesBurned = 0,
                isWorkoutFinished = false,
                isNewPR = false,
                newPRMessage = ""
            )
        }
        startGetReadyCountdown(3)
    }

    fun startRoutine(routine: RoutineEntity) {
        viewModelScope.launch {
            val exerciseIds = routineDao.getExerciseIdsForRoutine(routine.id)
            val routineExerciseList = _exercises.value.filter { exerciseIds.contains(it.id) }
            val firstExercise = routineExerciseList.firstOrNull() ?: DefaultExercises.list.first()

            stopTimers()
            _activeWorkoutState.update {
                ActiveWorkoutUiState(
                    activeExercise = firstExercise,
                    routineName = routine.name,
                    routineExercises = if (routineExerciseList.isNotEmpty()) routineExerciseList else listOf(firstExercise),
                    currentExerciseIndex = 0,
                    isRoutineMode = true,
                    isWorkoutActive = true,
                    isPaused = false,
                    isResting = false,
                    isCountingDown = true,
                    countdownSeconds = 3,
                    timerSeconds = 0,
                    currentSetIndex = 1,
                    completedReps = 0,
                    currentWeightKg = if (firstExercise.equipment == EquipmentType.BODYWEIGHT) 0.0 else 10.0,
                    totalVolumeKg = 0.0,
                    caloriesBurned = 0,
                    isWorkoutFinished = false,
                    isNewPR = false,
                    newPRMessage = ""
                )
            }
            startGetReadyCountdown(3)
        }
    }

    fun nextExercise() {
        val currentState = _activeWorkoutState.value
        val nextIdx = currentState.currentExerciseIndex + 1
        if (nextIdx < currentState.routineExercises.size) {
            val nextEx = currentState.routineExercises[nextIdx]
            stopTimers()
            _activeWorkoutState.update { state ->
                state.copy(
                    activeExercise = nextEx,
                    currentExerciseIndex = nextIdx,
                    currentSetIndex = 1,
                    completedReps = 0,
                    currentWeightKg = if (nextEx.equipment == EquipmentType.BODYWEIGHT) 0.0 else 10.0,
                    isResting = false,
                    restTimerSeconds = 0,
                    isCountingDown = true,
                    countdownSeconds = 3
                )
            }
            startGetReadyCountdown(3)
        }
    }

    fun saveCustomRoutine(routineName: String, selectedExerciseIds: List<String>, onComplete: () -> Unit) {
        if (routineName.isBlank() || selectedExerciseIds.isEmpty()) return
        viewModelScope.launch {
            val routine = RoutineEntity(name = routineName.trim())
            routineDao.insertRoutineWithExercises(routine, selectedExerciseIds)
            onComplete()
        }
    }

    fun updateCustomRoutine(routineId: Long, routineName: String, selectedExerciseIds: List<String>, onComplete: () -> Unit) {
        if (routineName.isBlank() || selectedExerciseIds.isEmpty()) return
        viewModelScope.launch {
            routineDao.updateRoutineWithExercises(routineId, routineName.trim(), selectedExerciseIds)
            onComplete()
        }
    }

    fun getRoutineWithExerciseIds(routineId: Long, onResult: (RoutineEntity?, List<String>) -> Unit) {
        viewModelScope.launch {
            val routine = routineDao.getRoutineById(routineId)
            val exerciseIds = routineDao.getExerciseIdsForRoutine(routineId)
            onResult(routine, exerciseIds)
        }
    }

    fun toggleExerciseInRoutine(routineId: Long, exerciseId: String, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val routine = routineDao.getRoutineById(routineId) ?: return@launch
            val currentIds = routineDao.getExerciseIdsForRoutine(routineId).toMutableList()
            if (currentIds.contains(exerciseId)) {
                currentIds.remove(exerciseId)
            } else {
                currentIds.add(exerciseId)
            }
            routineDao.updateRoutineWithExercises(routineId, routine.name, currentIds)
            onComplete()
        }
    }

    fun deleteCustomRoutine(routineId: Long) {
        viewModelScope.launch {
            routineDao.deleteRoutine(routineId)
        }
    }

    fun fetchExercisesForRoutine(routineId: Long, onResult: (List<Exercise>) -> Unit) {
        viewModelScope.launch {
            val exerciseIds = routineDao.getExerciseIdsForRoutine(routineId)
            val routineExerciseList = _exercises.value.filter { exerciseIds.contains(it.id) }
            onResult(routineExerciseList)
        }
    }

    fun pauseOrResumeWorkout() {
        val currentState = _activeWorkoutState.value
        if (!currentState.isWorkoutActive) return

        val newPaused = !currentState.isPaused
        _activeWorkoutState.update { it.copy(isPaused = newPaused) }

        if (newPaused) {
            workoutTimerJob?.cancel()
            restTimerJob?.cancel()
        } else {
            if (currentState.isResting) {
                startRestTimer()
            } else {
                startWorkoutTimer()
            }
        }
    }

    fun updateReps(delta: Int) {
        _activeWorkoutState.update { state ->
            val newReps = (state.completedReps + delta).coerceAtLeast(0)
            state.copy(completedReps = newReps)
        }
    }

    fun updateWeight(deltaKg: Double) {
        _activeWorkoutState.update { state ->
            val newWeight = (state.currentWeightKg + deltaKg).coerceAtLeast(0.0)
            state.copy(currentWeightKg = newWeight)
        }
    }

    fun logSetAndStartRest(restSeconds: Int = 30) {
        val state = _activeWorkoutState.value
        val currentExercise = state.activeExercise ?: return
        val exerciseReps = if (state.completedReps > 0) state.completedReps else currentExercise.defaultReps
        val setVolume = exerciseReps * state.currentWeightKg
        val addedCalories = (exerciseReps * 0.4).toInt() + 2

        viewModelScope.launch {
            checkAndRecordPR(currentExercise.id, state.currentWeightKg, exerciseReps, state.timerSeconds)
        }

        _activeWorkoutState.update { currentState ->
            currentState.copy(
                currentSetIndex = currentState.currentSetIndex + 1,
                completedReps = currentState.completedReps + exerciseReps,
                totalVolumeKg = currentState.totalVolumeKg + setVolume,
                caloriesBurned = currentState.caloriesBurned + addedCalories,
                isResting = true,
                restTimerSeconds = restSeconds,
                totalRestSeconds = restSeconds
            )
        }
        VoiceCoachHelper.speakRestStart(restSeconds, _isVoiceCoachEnabled.value)
        startRestTimer()
    }

    fun addRestSeconds(seconds: Int) {
        _activeWorkoutState.update { state ->
            if (state.isResting) {
                val newRest = (state.restTimerSeconds + seconds).coerceAtLeast(1)
                val newTotal = (state.totalRestSeconds + seconds).coerceAtLeast(1)
                state.copy(restTimerSeconds = newRest, totalRestSeconds = newTotal)
            } else state
        }
    }

    fun skipRest() {
        restTimerJob?.cancel()
        _activeWorkoutState.update { it.copy(isResting = false, restTimerSeconds = 0) }
        startWorkoutTimer()
    }

    fun finishWorkout() {
        stopTimers()
        val currentState = _activeWorkoutState.value
        if (!currentState.isWorkoutActive || currentState.activeExercise == null) return

        val exercise = currentState.activeExercise
        val logName = if (currentState.isRoutineMode) "Routine: ${currentState.routineName}" else exercise.name
        val durationMinutes = (currentState.timerSeconds / 60).coerceAtLeast(1)
        val finalReps = if (currentState.completedReps > 0) currentState.completedReps else 30
        val finalVolume = currentState.totalVolumeKg
        val finalCalories = if (currentState.caloriesBurned > 0) currentState.caloriesBurned else durationMinutes * 8

        val entity = CompletedWorkoutEntity(
            routineName = logName,
            timestamp = System.currentTimeMillis(),
            durationMinutes = durationMinutes,
            totalReps = finalReps,
            totalVolumeKg = finalVolume,
            caloriesBurned = finalCalories
        )

        viewModelScope.launch {
            workoutDao.insertWorkout(entity)
            checkAndRecordPR(exercise.id, currentState.currentWeightKg, finalReps, currentState.timerSeconds)
        }

        VoiceCoachHelper.speakWorkoutComplete(_isVoiceCoachEnabled.value)

        _activeWorkoutState.update {
            it.copy(
                isWorkoutActive = false,
                isPaused = false,
                isResting = false,
                isWorkoutFinished = true
            )
        }
    }

    fun resetWorkoutState() {
        stopTimers()
        _activeWorkoutState.value = ActiveWorkoutUiState()
    }

    fun toggleReminder(enabled: Boolean) {
        _isReminderEnabled.value = enabled
        prefs.edit().putBoolean(ReminderManager.KEY_ENABLED, enabled).apply()
        if (enabled) {
            ReminderManager.scheduleDailyReminder(getApplication())
        } else {
            ReminderManager.cancelDailyReminder(getApplication())
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        _reminderHour.value = hour
        _reminderMinute.value = minute
        prefs.edit().putInt(ReminderManager.KEY_HOUR, hour).putInt(ReminderManager.KEY_MINUTE, minute).apply()
        if (_isReminderEnabled.value) {
            ReminderManager.scheduleDailyReminder(getApplication())
        }
    }

    fun toggleReminderDay(dayOfWeek: Int) {
        _activeDaysSet.update { current ->
            val updated = if (current.contains(dayOfWeek)) current - dayOfWeek else current + dayOfWeek
            val daysStr = updated.joinToString(",")
            prefs.edit().putString(ReminderManager.KEY_ACTIVE_DAYS, daysStr).apply()
            updated
        }
    }

    fun setReminderTheme(theme: String) {
        _reminderTheme.value = theme
        prefs.edit().putString(ReminderManager.KEY_THEME, theme).apply()
    }

    private suspend fun checkAndRecordPR(exerciseId: String, weightKg: Double, reps: Int, durationSecs: Int) {
        val existingPR = prDao.getPRForExercise(exerciseId)
        val currentMaxWeight = existingPR?.maxWeightKg ?: 0.0
        val currentMaxReps = existingPR?.maxReps ?: 0
        val currentMaxDuration = existingPR?.maxDurationSeconds ?: 0

        var isNewRecord = false
        var message = ""

        if (weightKg > currentMaxWeight && weightKg > 0) {
            isNewRecord = true
            message = "New Max Weight: ${weightKg.toInt()} kg!"
        } else if (reps > currentMaxReps && reps > 0) {
            isNewRecord = true
            message = "New Rep Record: $reps reps!"
        } else if (durationSecs > currentMaxDuration && durationSecs > 30) {
            isNewRecord = true
            message = "New Duration Record: ${durationSecs}s!"
        }

        if (isNewRecord) {
            val updatedPR = PersonalRecordEntity(
                exerciseId = exerciseId,
                maxWeightKg = maxOf(weightKg, currentMaxWeight),
                maxReps = maxOf(reps, currentMaxReps),
                maxDurationSeconds = maxOf(durationSecs, currentMaxDuration),
                updatedAt = System.currentTimeMillis()
            )
            prDao.upsertPR(updatedPR)
            _activeWorkoutState.update { it.copy(isNewPR = true, newPRMessage = message) }
        }
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                _activeWorkoutState.update { state ->
                    if (!state.isPaused && !state.isResting) {
                        val newTimer = state.timerSeconds + 1
                        val calories = if (newTimer % 10 == 0) state.caloriesBurned + 1 else state.caloriesBurned
                        state.copy(timerSeconds = newTimer, caloriesBurned = calories)
                    } else state
                }
            }
        }
    }

    private fun startGetReadyCountdown(initialSeconds: Int = 3) {
        countdownTimerJob?.cancel()
        _activeWorkoutState.update { it.copy(isCountingDown = true, countdownSeconds = initialSeconds) }
        AudioHapticHelper.playCountdownTick(getApplication())
        val currentExName = _activeWorkoutState.value.activeExercise?.name ?: "Workout"
        VoiceCoachHelper.speakGetReady(currentExName, _isVoiceCoachEnabled.value)

        countdownTimerJob = viewModelScope.launch {
            var remaining = initialSeconds
            while (remaining > 0) {
                delay(1000L)
                val currentState = _activeWorkoutState.value
                if (!currentState.isPaused) {
                    remaining--
                    if (remaining > 0) {
                        AudioHapticHelper.playCountdownTick(getApplication())
                        VoiceCoachHelper.speakCountdown(remaining, _isVoiceCoachEnabled.value)
                        _activeWorkoutState.update { it.copy(countdownSeconds = remaining) }
                    } else {
                        AudioHapticHelper.playCountdownGo(getApplication())
                        VoiceCoachHelper.speakCountdown(0, _isVoiceCoachEnabled.value)
                        _activeWorkoutState.update { it.copy(isCountingDown = false, countdownSeconds = 0) }
                        startWorkoutTimer()
                        break
                    }
                }
            }
        }
    }

    fun skipCountdown() {
        countdownTimerJob?.cancel()
        AudioHapticHelper.playCountdownGo(getApplication())
        VoiceCoachHelper.speakCountdown(0, _isVoiceCoachEnabled.value)
        _activeWorkoutState.update { it.copy(isCountingDown = false, countdownSeconds = 0) }
        startWorkoutTimer()
    }

    private fun startRestTimer() {
        restTimerJob?.cancel()
        restTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val currentState = _activeWorkoutState.value
                if (!currentState.isPaused && currentState.isResting) {
                    if (currentState.restTimerSeconds > 1) {
                        _activeWorkoutState.update { it.copy(restTimerSeconds = it.restTimerSeconds - 1) }
                    } else {
                        AudioHapticHelper.playRestCompleteFeedback(getApplication())
                        _activeWorkoutState.update { it.copy(isResting = false, restTimerSeconds = 0) }
                        startGetReadyCountdown(3)
                        break
                    }
                }
            }
        }
    }

    private fun stopTimers() {
        workoutTimerJob?.cancel()
        restTimerJob?.cancel()
        countdownTimerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        VoiceCoachHelper.shutdown()
    }
}
