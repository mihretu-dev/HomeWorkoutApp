package com.base.androidstartertemplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.base.androidstartertemplate.themes.theme.AppTheme
import com.base.androidstartertemplate.ui.navigation.BottomNavItem
import com.base.androidstartertemplate.ui.screens.ActiveWorkoutScreen
import com.base.androidstartertemplate.ui.screens.AnalyticsTabScreen
import com.base.androidstartertemplate.ui.screens.CreateRoutineScreen
import com.base.androidstartertemplate.ui.screens.ExerciseDetailScreen
import com.base.androidstartertemplate.ui.screens.SettingsTabScreen
import com.base.androidstartertemplate.ui.screens.WorkoutsTabScreen
import com.base.androidstartertemplate.ui.viewmodel.WorkoutViewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.base.androidstartertemplate.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                var showSplash by remember { mutableStateOf(true) }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (showSplash) {
                        SplashScreen(onSplashFinished = { showSplash = false })
                    } else {
                        HomeWorkoutAppUI()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeWorkoutAppUI(workoutViewModel: WorkoutViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTabRoute = currentRoute in BottomNavItem.items.map { it.route }

    Scaffold(
        bottomBar = {
            if (isTabRoute) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    BottomNavItem.items.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title
                                )
                            },
                            label = {
                                Text(
                                    text = item.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Workouts.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Workouts.route) {
                WorkoutsTabScreen(
                    viewModel = workoutViewModel,
                    onStartWorkout = { exercise ->
                        workoutViewModel.startWorkout(exercise)
                        navController.navigate("active_workout")
                    },
                    onExerciseClick = { exerciseId ->
                        navController.navigate("exercise_detail/$exerciseId")
                    },
                    onCreateRoutineClick = {
                        navController.navigate("create_routine")
                    },
                    onEditRoutineClick = { routineId ->
                        navController.navigate("create_routine?routineId=$routineId")
                    },
                    onStartRoutine = { routine ->
                        workoutViewModel.startRoutine(routine)
                        navController.navigate("active_workout")
                    }
                )
            }

            composable(BottomNavItem.Analytics.route) {
                AnalyticsTabScreen(viewModel = workoutViewModel)
            }

            composable(BottomNavItem.Settings.route) {
                SettingsTabScreen(viewModel = workoutViewModel)
            }

            composable(
                route = "exercise_detail/{exerciseId}",
                arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
            ) { backStackEntry ->
                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                ExerciseDetailScreen(
                    exerciseId = exerciseId,
                    viewModel = workoutViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onStartWorkout = { exercise ->
                        workoutViewModel.startWorkout(exercise)
                        navController.navigate("active_workout")
                    }
                )
            }

            composable("active_workout") {
                ActiveWorkoutScreen(
                    viewModel = workoutViewModel,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(
                route = "create_routine?routineId={routineId}",
                arguments = listOf(
                    navArgument("routineId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getLong("routineId") ?: -1L
                CreateRoutineScreen(
                    viewModel = workoutViewModel,
                    routineIdToEdit = if (routineId != -1L) routineId else null,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
