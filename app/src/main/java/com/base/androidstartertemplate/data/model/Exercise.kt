package com.base.androidstartertemplate.data.model

import com.base.androidstartertemplate.R

data class Exercise(
    val id: String,
    val name: String,
    val equipment: EquipmentType,
    val targetMuscle: String,
    val defaultReps: Int,
    val durationSeconds: Int = 0,
    val description: String
) {
    fun getGifAssetPath(): String {
        return "file:///android_asset/exercises/${id}.gif"
    }
}

object DefaultExercises {
    val list = listOf(
        // BODYWEIGHT & PUSH-UP VARIATIONS
        Exercise(
            id = "push_ups",
            name = "Push-Ups",
            equipment = EquipmentType.BODYWEIGHT,
            targetMuscle = "Chest",
            defaultReps = 15,
            description = "Keep your body rigid in a plank position and press down and up from the floor."
        ),
        Exercise(
            id = "diamond_push_ups",
            name = "Diamond Push-Ups",
            equipment = EquipmentType.BODYWEIGHT,
            targetMuscle = "Arms",
            defaultReps = 12,
            description = "Place hands close together beneath your chest forming a diamond shape with index fingers and thumbs to overload triceps."
        ),
        Exercise(
            id = "wide_push_ups",
            name = "Wide Push-Ups",
            equipment = EquipmentType.BODYWEIGHT,
            targetMuscle = "Chest",
            defaultReps = 15,
            description = "Set hands wider than shoulder-width to place emphasis on outer pectoral muscles."
        ),
        Exercise(
            id = "decline_push_ups",
            name = "Decline Push-Ups",
            equipment = EquipmentType.BODYWEIGHT,
            targetMuscle = "Chest",
            defaultReps = 12,
            description = "Elevate feet on a chair or bench to shift load onto upper chest and shoulders."
        ),
        Exercise(
            id = "archer_push_ups",
            name = "Archer Push-Ups",
            equipment = EquipmentType.BODYWEIGHT,
            targetMuscle = "Chest",
            defaultReps = 10,
            description = "Extend one arm straight to the side while lowering your body over the working arm in an archer motion."
        ),
        Exercise(
            id = "pike_push_ups",
            name = "Pike Push-Ups",
            equipment = EquipmentType.BODYWEIGHT,
            targetMuscle = "Shoulders",
            defaultReps = 10,
            description = "Hinge hips up high into an inverted V-shape and press downwards toward the floor to target anterior deltoids."
        ),
        Exercise(
            id = "clap_push_ups",
            name = "Clap Push-Ups",
            equipment = EquipmentType.BODYWEIGHT,
            targetMuscle = "Chest",
            defaultReps = 8,
            description = "Explode upwards off the ground with enough power to clap hands together before landing softly back into a rep."
        ),
        Exercise(
            id = "planks",
            name = "Planks",
            equipment = EquipmentType.BODYWEIGHT,
            targetMuscle = "Core",
            defaultReps = 0,
            durationSeconds = 60,
            description = "Maintain a straight body position resting on your forearms and toes while bracing your core."
        ),

        // PULL-UP BAR EXERCISES
        Exercise(
            id = "pull_ups",
            name = "Pull-Ups",
            equipment = EquipmentType.PULL_UP_BAR,
            targetMuscle = "Back",
            defaultReps = 10,
            description = "Hang from the pull-up bar with an overhand grip and pull your chest up until your chin clears the bar."
        ),
        Exercise(
            id = "chin_ups",
            name = "Chin-Ups",
            equipment = EquipmentType.PULL_UP_BAR,
            targetMuscle = "Arms",
            defaultReps = 10,
            description = "Grip the bar underhand with palms facing you to heavily engage the biceps and lower lats."
        ),
        Exercise(
            id = "wide_grip_pull_ups",
            name = "Wide-Grip Pull-Ups",
            equipment = EquipmentType.PULL_UP_BAR,
            targetMuscle = "Back",
            defaultReps = 8,
            description = "Use an extra wide overhand grip to maximize lat width and upper back recruitment."
        ),
        Exercise(
            id = "commando_pull_ups",
            name = "Commando Pull-Ups",
            equipment = EquipmentType.PULL_UP_BAR,
            targetMuscle = "Back",
            defaultReps = 8,
            description = "Stand sideways beneath bar with neutral staggered grip, pulling head up to alternating sides of the bar."
        ),
        Exercise(
            id = "l_sit_pull_ups",
            name = "L-Sit Pull-Ups",
            equipment = EquipmentType.PULL_UP_BAR,
            targetMuscle = "Core",
            defaultReps = 6,
            description = "Extend legs parallel to the floor in an L-position while executing strict pull-ups."
        ),
        Exercise(
            id = "hanging_leg_raises",
            name = "Hanging Leg Raises",
            equipment = EquipmentType.PULL_UP_BAR,
            targetMuscle = "Core",
            defaultReps = 12,
            description = "Hang straight from bar and raise extended legs up to 90 degrees or touch toes to the bar."
        ),
        Exercise(
            id = "windshield_wipers",
            name = "Windshield Wipers",
            equipment = EquipmentType.PULL_UP_BAR,
            targetMuscle = "Core",
            defaultReps = 10,
            description = "Invert upside down on pull-up bar with legs straight up, rotating legs side-to-side like wipers."
        ),

        // DIP STATION EXERCISES
        Exercise(
            id = "dips",
            name = "Dips",
            equipment = EquipmentType.DIP_STATION,
            targetMuscle = "Arms",
            defaultReps = 12,
            description = "Grasp dip bars, lower your body by bending your elbows to 90 degrees, and push back up explosively."
        ),
        Exercise(
            id = "chest_dips",
            name = "Chest Dips",
            equipment = EquipmentType.DIP_STATION,
            targetMuscle = "Chest",
            defaultReps = 10,
            description = "Lean torso forward 45 degrees with knees bent back to shift tension onto lower chest."
        ),
        Exercise(
            id = "tricep_dips",
            name = "Tricep Dips",
            equipment = EquipmentType.DIP_STATION,
            targetMuscle = "Arms",
            defaultReps = 12,
            description = "Keep torso completely vertical and elbows tucked tight to sides to isolate the triceps."
        ),
        Exercise(
            id = "straight_bar_dips",
            name = "Straight Bar Dips",
            equipment = EquipmentType.DIP_STATION,
            targetMuscle = "Chest",
            defaultReps = 8,
            description = "Mount a single straight bar and lower chest to bar before pushing up."
        ),
        Exercise(
            id = "korean_dips",
            name = "Korean Dips",
            equipment = EquipmentType.DIP_STATION,
            targetMuscle = "Arms",
            defaultReps = 6,
            description = "Dip behind your back on a single bar, driving shoulders and triceps into deep stretch."
        ),
        Exercise(
            id = "hanging_knee_raises_dip",
            name = "Hanging Knee Raises (Dip Bar)",
            equipment = EquipmentType.DIP_STATION,
            targetMuscle = "Core",
            defaultReps = 15,
            description = "Lock out arms on dip station and tuck knees up to chest in a controlled rhythm."
        ),

        // DUMBBELL EXERCISES
        Exercise(
            id = "db_bench_press",
            name = "Dumbbell Bench Press",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Chest",
            defaultReps = 10,
            description = "Lie flat holding dumbbells at chest height and press them up until arms are fully extended."
        ),
        Exercise(
            id = "incline_db_press",
            name = "Incline Dumbbell Press",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Chest",
            defaultReps = 10,
            description = "Lie on an inclined bench (30-45 deg) and press dumbbells upward to target upper pectoral fibers."
        ),
        Exercise(
            id = "db_flyes",
            name = "Dumbbell Flyes",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Chest",
            defaultReps = 12,
            description = "Lie flat on a bench, open arms wide with slight elbow bend to feel a deep chest stretch, then bring dumbbells together."
        ),
        Exercise(
            id = "single_arm_db_row",
            name = "Single-Arm Dumbbell Row",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Back",
            defaultReps = 10,
            description = "Support knee and hand on a bench, pull dumbbell to your hip, driving through elbow while squeezing Lats."
        ),
        Exercise(
            id = "db_overhead_press",
            name = "Dumbbell Overhead Press",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Shoulders",
            defaultReps = 10,
            description = "Seated or standing, hold dumbbells at shoulder level and press vertically overhead."
        ),
        Exercise(
            id = "db_lateral_raise",
            name = "Dumbbell Lateral Raise",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Shoulders",
            defaultReps = 15,
            description = "Stand with dumbbells at sides, raise arms outward until parallel to floor focusing on side delts."
        ),
        Exercise(
            id = "db_bicep_curl",
            name = "Dumbbell Bicep Curl",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Arms",
            defaultReps = 12,
            description = "Stand tall with arms extended, curl dumbbells upwards while supinating wrists at top."
        ),
        Exercise(
            id = "db_hammer_curl",
            name = "Dumbbell Hammer Curl",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Arms",
            defaultReps = 12,
            description = "Hold dumbbells with neutral thumbs-up grip and curl towards shoulders for forearm and bicep thickness."
        ),
        Exercise(
            id = "overhead_db_tricep_ext",
            name = "Overhead Dumbbell Tricep Extension",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Arms",
            defaultReps = 12,
            description = "Hold one heavy dumbbell overhead with both hands, lower it behind your head, and extend back up."
        ),
        Exercise(
            id = "db_goblet_squat",
            name = "Dumbbell Goblet Squat",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Legs",
            defaultReps = 12,
            description = "Hold a dumbbell vertically against chest and perform deep squats with heels flat on ground."
        ),
        Exercise(
            id = "db_walking_lunge",
            name = "Dumbbell Walking Lunge",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Legs",
            defaultReps = 12,
            description = "Hold dumbbells by sides and step forward into alternating lunges maintaining upright torso posture."
        ),
        Exercise(
            id = "db_bulgarian_split_squat",
            name = "Dumbbell Bulgarian Split Squat",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Legs",
            defaultReps = 10,
            description = "Elevate one foot behind you on a bench and lower your hips on the front leg until front thigh is parallel."
        ),
        Exercise(
            id = "db_standing_calf_raise",
            name = "Dumbbell Standing Calf Raise",
            equipment = EquipmentType.DUMBBELL,
            targetMuscle = "Legs",
            defaultReps = 15,
            description = "Hold dumbbells by your sides, stand on a step edge, lower heels down then push up high on tiptoes."
        ),

        // BARBELL EXERCISES
        Exercise(
            id = "barbell_bench_press",
            name = "Barbell Bench Press",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Chest",
            defaultReps = 8,
            description = "Unrack barbell flat on bench, touch bar to mid-chest, and press up with drive."
        ),
        Exercise(
            id = "incline_barbell_press",
            name = "Incline Barbell Press",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Chest",
            defaultReps = 8,
            description = "Set bench to 30-45 deg incline, unrack barbell, lower to upper chest and drive upwards."
        ),
        Exercise(
            id = "barbell_bent_over_row",
            name = "Barbell Bent-Over Row",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Back",
            defaultReps = 8,
            description = "Hinge forward at 45 degrees with flat spine, pull barbell to lower chest/navel area."
        ),
        Exercise(
            id = "barbell_rdl",
            name = "Barbell Romanian Deadlift (RDL)",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Legs",
            defaultReps = 10,
            description = "Hinge hips back with slight knee bend, lowering barbell along shins until hamstrings stretch fully."
        ),
        Exercise(
            id = "barbell_military_press",
            name = "Barbell Military Press",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Shoulders",
            defaultReps = 8,
            description = "Stand firm, press barbell overhead from collarbone to lockout without swinging hips."
        ),
        Exercise(
            id = "barbell_bicep_curl",
            name = "Barbell Bicep Curl",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Arms",
            defaultReps = 10,
            description = "Grip barbell underhand at shoulder-width and curl weight toward shoulders with strict form."
        ),
        Exercise(
            id = "barbell_skullcrusher",
            name = "Barbell Skullcrusher",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Arms",
            defaultReps = 10,
            description = "Lie on bench holding EZ/straight bar overhead, bend elbows to lower bar near forehead and extend."
        ),
        Exercise(
            id = "barbell_back_squat",
            name = "Barbell Back Squat",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Legs",
            defaultReps = 8,
            description = "Rest barbell across upper traps, squat down below parallel and drive through heels to stand."
        ),
        Exercise(
            id = "barbell_front_squat",
            name = "Barbell Front Squat",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Legs",
            defaultReps = 8,
            description = "Rack barbell across front shoulders/fingertips with elbows high, perform deep squat keeping torso vertical."
        ),
        Exercise(
            id = "barbell_conventional_deadlift",
            name = "Barbell Conventional Deadlift",
            equipment = EquipmentType.BARBELL,
            targetMuscle = "Legs",
            defaultReps = 6,
            description = "Set shins against barbell, grip bar tight, drive legs into floor and lock out hips vertically."
        )
    )
}
