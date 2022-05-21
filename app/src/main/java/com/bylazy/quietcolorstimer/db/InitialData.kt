package com.bylazy.quietcolorstimer.db

import androidx.compose.ui.graphics.Color
import com.bylazy.quietcolorstimer.data.*

val test_timer_1 = InTimer(name = "Test timer 1",
    description = "Short description", link = "", pinned = false, type = TimerType.WORKOUT)

/*val test_timer_2 = InTimer(name = "Test timer 2",
    description = "Some description", link = "link here", pinned = false, type = TimerType.OTHER)*/

val test_timer_1_intervals = listOf(Interval(timerId = 0,
    position = 1,
    name = "Interval 1",
    duration = 33,
    color = Color.Green.string(),
    signal = IntervalSignal.SILENT,
    sound = IntervalSound.KNUCKLE,
    customSoundUri = "",
    type = IntervalType.BRIGHT),
    Interval(timerId = 0,
    position = 2,
    name = "Interval 2",
    duration = 55,
    color = Color.Blue.string(),
    signal = IntervalSignal.SILENT,
    sound = IntervalSound.KNUCKLE,
    customSoundUri = "",
    type = IntervalType.DARK)
)

/*val test_timer_2_intervals = listOf(Interval(timerId = 0,
    position = 1,
    name = "Interval 3",
    duration = 120,
    color = Color.Red.string(),
    signal = IntervalSignal.SILENT,
    sound = IntervalSound.KNUCKLE,
    customSoundUri = "",
    type = IntervalType.BRIGHT),
    Interval(timerId = 0,
    position = 2,
    name = "Interval 4",
    duration = 380,
    color = Color.Yellow.string(),
    signal = IntervalSignal.SILENT,
    sound = IntervalSound.KNUCKLE,
    customSoundUri = "",
    type = IntervalType.DARK)
)*/

val initial_timer_yoga_1 = InTimer(name = "Coherent Breathing",
    description = """
    Coherent Breathing is a form of breathing that involves taking long slow breaths at a rate of about five per minute. Coherent breathing, or deep breathing, helps to calm the body through its effect on the autonomic nervous system.
    
    Whether it is practiced as part of yoga or meditation, or simply on its own as a relaxation strategy, coherent breathing is a simple and easy way to reduce stress and calm down when feeling anxious.
    
    Lets get started:
    1. Find a comfortable position to practice coherent breathing. Place one hand on your stomach.
    2. Breath in for four seconds and then out for four seconds. Do this for one minute.
    3. Repeat, but extend your inhales and exhales to five seconds.
    4. Repeat again, extending further to six seconds, and so on.
        
    During this process, keep your hand on your stomach to make sure that you are breathing deeply from your diaphragm and not shallowly from your chest.
    """.trimIndent(),
    link = "https://www.verywellmind.com/an-overview-of-coherent-breathing-4178943",
    pinned = false,
    type = TimerType.YOGA)

val initial_timer_yoga_1_intervals = (0..13).map { i ->
    if (i%2==0) Interval(timerId = 0,
        position = i+1,
        name = "Inhale",
        duration = 4,
        color = Color(0xFF5885AF).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
    else Interval(timerId = 0,
        position = i+1,
        name = "Exhale",
        duration = 4,
        color = Color(0xFF8BCD50).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
} + (14..25).map { i ->
    if (i%2==0) Interval(timerId = 0,
        position = i+1,
        name = "Inhale",
        duration = 5,
        color = Color(0xFF5885AF).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
    else Interval(timerId = 0,
        position = i+1,
        name = "Exhale",
        duration = 5,
        color = Color(0xFF8BCD50).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
} + (26..35).map { i ->
    if (i%2==0) Interval(timerId = 0,
        position = i+1,
        name = "Inhale",
        duration = 6,
        color = Color(0xFF5885AF).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
    else Interval(timerId = 0,
        position = i+1,
        name = "Exhale",
        duration = 6,
        color = Color(0xFF8BCD50).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
} + (36..43).map { i ->
    if (i%2==0) Interval(timerId = 0,
        position = i+1,
        name = "Inhale",
        duration = 7,
        color = Color(0xFF5885AF).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
    else Interval(timerId = 0,
        position = i+1,
        name = "Exhale",
        duration = 7,
        color = Color(0xFF8BCD50).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
} + (44..51).map { i ->
    if (i%2==0) Interval(timerId = 0,
        position = i+1,
        name = "Inhale",
        duration = 8,
        color = Color(0xFF5885AF).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
    else Interval(timerId = 0,
        position = i+1,
        name = "Exhale",
        duration = 8,
        color = Color(0xFF8BCD50).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
}

val initial_timer_yoga_2 = InTimer(name = "Relaxing Breathing",
    description = """
    The 4-4-8 Breathing technique is great to use when you feel stressed or tense because it can help to calm the nervous system, clear the head of distractions and reduce stress.
        
    • While sitting, breathe in through your nose for (4 sec), taking the breath into your stomach.
        
    • Hold your breath (4 sec).
        
    • Release your breath through your mouth with a whooshing sound (8 sec).
        
    • Without a break, breathe in again, repeating the entire technique 3-10 times in a row.
        
    • Focus on counting when breathing in, holding the breath, and breathing out.
    """.trimIndent(),
    link = "https://www.powerbreathe.com/breathing-for-focus-using-the-4-4-8-breathing-technique/",
    pinned = false,
    type = TimerType.YOGA)

val initial_timer_yoga_2_intervals = (1..30).mapIndexed { _, i ->
    when (i%3) {
        1 -> Interval(timerId = 0,
            position = i,
            name = "Inhale",
            duration = 4,
            color = Color(0xFF5885AF).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT)
        2 -> Interval(timerId = 0,
            position = i,
            name = "Hold",
            duration = 4,
            color = Color(0xFFFBEE0F).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT)
        else -> Interval(timerId = 0,
            position = i,
            name = "Exhale",
            duration = 8,
            color = Color(0xFF8BCD50).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT)
    }
}

val initial_timer_yoga_3 = InTimer(name = "Box Breathing",
    description = """
    Box breathing, also referred to as square breathing, is a deep breathing technique that can help you slow down your breathing. It works by distracting your mind as you count to four, calming your nervous system, and decreasing stress in your body.
    
    Four Steps to Master Box Breathing

    Step 1: Breathe in counting to four slowly. Feel the air enter your lungs.
    Step 2: Hold your breath for 4 seconds. Try to avoid inhaling or exhaling for 4 seconds.
    Step 3: Slowly exhale through your mouth for 4 seconds.
    Step 4. Hold your breath for 4 seconds.
    Step 5: Repeat steps 1 to 4 until you feel re-centered.
    
    Be aware of your breath to ensure that you are taking deep breaths, allowing your stomach to rise.
    """.trimIndent(),
    link = "https://www.webmd.com/balance/what-is-box-breathing",
    pinned = false,
    type = TimerType.YOGA)

val initial_timer_yoga_3_intervals = (1..32).mapIndexed { _, i ->
    when (i%4) {
        1 -> Interval(timerId = 0,
            position = i,
            name = "Inhale",
            duration = 4,
            color = Color(0xFF5885AF).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT)
        2 -> Interval(timerId = 0,
            position = i,
            name = "Hold",
            duration = 4,
            color = Color(0xFFFBEE0F).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT)
        3 -> Interval(timerId = 0,
            position = i,
            name = "Exhale",
            duration = 4,
            color = Color(0xFF8BCD50).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT)
        else -> Interval(timerId = 0,
            position = i,
            name = "Hold",
            duration = 4,
            color = Color(0xFFFBEE0F).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT)
    }
}

//10-Minute Yoga

val initial_timer_yoga_4 = InTimer(name = "10-Minute Yoga",
    description = """
    The workout is full of forward folds and inversions, which calm the nervous system and reduce stress. By flipping yourself upside-down, you're bringing fresh blood and oxygen to the brain, she adds, which research suggests may have numerous benefits, including helping you think more clearly, be more alert, and improve memory and focus. Yogis also say that inversions can shift your perspective (you're literally looking at life from a different angle), and since they take a lot of focus, force you to be present and stop worrying about your to-do list or any deadlines creeping up. And, of course, research points toward yoga in general as an effective method for managing stress and reducing anxiety.
    
    Ready to chill out? Grab a mat and a block (if you have one) and get ready to feel Zen after just 10 minutes.
    
    1. Child's Pose
    2. Downward Dog
    3. Wide-Legged Fold
    4. Headstand (or Low Lunge)
    5. Pigeon Right Leg
    6. Pigeon Left Leg
    7. Supported Bridge
    """.trimIndent(),
    link = "https://www.self.com/gallery/10-minute-yoga-routine-feel-less-stressed",
    pinned = false,
    type = TimerType.YOGA)

val initial_timer_yoga_4_intervals = listOf(
    Interval(timerId = 0,
        position = 1,
        name = "Child's Pose",
        duration = 40,
        color = Color(0xFFFBEE0F).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT),
    Interval(timerId = 0,
        position = 2,
        name = "Downward Dog",
        duration = 90,
        color = Color(0xFFE930C0).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT),
    Interval(timerId = 0,
        position = 3,
        name = "Fold",
        duration = 90,
        color = Color(0xFF41729F).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT),
    Interval(timerId = 0,
        position = 4,
        name = "Headstand",
        duration = 60,
        color = Color(0xFFFFC5D0).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT),
    Interval(timerId = 0,
        position = 5,
        name = "Pigeon Right",
        duration = 120,
        color = Color(0xFF81B622).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT),
    Interval(timerId = 0,
        position = 6,
        name = "Pigeon Left",
        duration = 120,
        color = Color(0xFF31D1D0).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT),
    Interval(timerId = 0,
        position = 7,
        name = "Bridge",
        duration = 120,
        color = Color(0xFFB637FB).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT)
)

val initial_timer_workout_1 = InTimer(
    name = "Tabata training 4 min",
    description = """
    Tabata is a method of high-intensity interval training that uses short work intervals and rest periods. It is considered very intense since the work intervals are meant to be performed with all-out effort and the rests between each work interval are minimal.
    
    Each Tabata round lasts 4 minutes and involves eight intervals of 20 seconds of intense exercise followed by 10 seconds of rest. Usually, you do a Tabata workout for 20 minutes, but you can opt to do one or a few exercises for a shorter session.
    
    Tabata workout may include:    
    • Mountain Climbers
    • High Knees
    • Burpees
    • Squat Upper-Cuts
    • Quick Feet
    • Power Squats
    • Squat Speed Bag
    """.trimIndent(),
    link = "https://www.webmd.com/fitness-exercise/features/the-4-minute-fat-loss-workout",
    pinned = false,
    type = TimerType.WORKOUT
)

val initial_timer_1_workout_intervals = listOf(
    Interval(
        timerId = 0,
        position = 1,
        name = "Warm Up",
        duration = 60,
        color = Color(0xFFFBEE0F).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 2,
        name = "Work",
        duration = 20,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.EXQUISITE,
        customSoundUri = "",
        type = IntervalType.BRIGHT
    ),
    Interval(
        timerId = 0,
        position = 3,
        name = "Rest",
        duration = 10,
        color = Color(0xFF3D550C).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 4,
        name = "Work",
        duration = 20,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.EXQUISITE,
        customSoundUri = "",
        type = IntervalType.BRIGHT
    ),
    Interval(
        timerId = 0,
        position = 5,
        name = "Rest",
        duration = 10,
        color = Color(0xFF3D550C).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 6,
        name = "Work",
        duration = 20,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.EXQUISITE,
        customSoundUri = "",
        type = IntervalType.BRIGHT
    ),
    Interval(
        timerId = 0,
        position = 7,
        name = "Rest",
        duration = 10,
        color = Color(0xFF3D550C).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 8,
        name = "Work",
        duration = 20,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.EXQUISITE,
        customSoundUri = "",
        type = IntervalType.BRIGHT
    ),
    Interval(
        timerId = 0,
        position = 9,
        name = "Rest",
        duration = 10,
        color = Color(0xFF3D550C).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 10,
        name = "Work",
        duration = 20,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.EXQUISITE,
        customSoundUri = "",
        type = IntervalType.BRIGHT
    ),
    Interval(
        timerId = 0,
        position = 11,
        name = "Rest",
        duration = 10,
        color = Color(0xFF3D550C).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 12,
        name = "Work",
        duration = 20,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.EXQUISITE,
        customSoundUri = "",
        type = IntervalType.BRIGHT
    ),
    Interval(
        timerId = 0,
        position = 13,
        name = "Rest",
        duration = 10,
        color = Color(0xFF3D550C).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 14,
        name = "Work",
        duration = 20,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.EXQUISITE,
        customSoundUri = "",
        type = IntervalType.BRIGHT
    ),
    Interval(
        timerId = 0,
        position = 15,
        name = "Rest",
        duration = 10,
        color = Color(0xFF3D550C).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 16,
        name = "Work",
        duration = 20,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.EXQUISITE,
        customSoundUri = "",
        type = IntervalType.BRIGHT
    ),
    Interval(
        timerId = 0,
        position = 17,
        name = "Rest",
        duration = 10,
        color = Color(0xFF3D550C).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    )
)

val initial_timer_workout_2 = InTimer(
    name = "60HIIT",
    description = """
    So, if you’re ready to try HIIT, be sure to do 60-second bursts of exercise followed by 60 seconds of rest.
    
    Choose an exercise from each of these categories:

    • Aerobics (for example you could run in place or do high knees or jumping jacks)
    • Abs (this could be sit-ups or planks)
    • Lower body (there are lots of options here, like squats and lunges)
    • Upper body (you could go with push-ups, tricep dips, or upper body free weights)
    
    Then, simply alternate between each exercise (60 seconds at a time) with a rest period after each.
    """.trimIndent(),
    link = "https://easyhealthoptions.com/30hiit-60hiit-wasting-time-high-intensity-interval-training/",
    pinned = false,
    type = TimerType.WORKOUT
)

val initial_timer_workout_2_intervals = (0..11).map {
    when {
        it == 0 -> Interval(
            timerId = 0,
            position = 1,
            name = "Warm Up",
            duration = 60,
            color = Color(0xFFFBEE0F).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT
        )
        it % 2 == 1 -> Interval(
            timerId = 0,
            position = it+1,
            name = "Work",
            duration = 60,
            color = Color(0xFF910C00).string(),
            signal = IntervalSignal.SOUND,
            sound = IntervalSound.EXQUISITE,
            customSoundUri = "",
            type = IntervalType.BRIGHT
        )
        else -> Interval(
            timerId = 0,
            position = it+1,
            name = "Rest",
            duration = 60,
            color = Color(0xFF3D550C).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT
        )
    }
}

val initial_timer_workout_3 = InTimer(
    name = "Plank",
    description = """
    Here’s how to do a plank correctly:

    • Lie facedown with your forearms on the floor, with your legs extended and your feet together. You can use a mat or towel to make this more comfortable.
    • Push into your forearms as you raise your body so it forms a straight line from your head and neck to your feet. (Do not let your hips rise or sag.)
    • Keep your gaze down and hold this position as you engage your abdominal muscles. Take steady, even breaths.
    • Try to maintain the position for up to 30 seconds and then lower your body and rest. This completes one set. Work toward completing two to three sets.
    """.trimIndent(),
    link = "https://www.health.harvard.edu/blog/straight-talk-on-planking-2019111318304",
    pinned = false,
    type = TimerType.WORKOUT
)

val initial_timer_workout_3_intervals = listOf(
    Interval(
        timerId = 0,
        position = 1,
        name = "Hold",
        duration = 20,
        color = Color(0xFFCC5216).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 2,
        name = "Rest",
        duration = 20,
        color = Color(0xFF81B622).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 3,
        name = "Hold",
        duration = 30,
        color = Color(0xFFCC5216).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 4,
        name = "Rest",
        duration = 20,
        color = Color(0xFF81B622).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 5,
        name = "Hold",
        duration = 40,
        color = Color(0xFFCC5216).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 6,
        name = "Rest",
        duration = 20,
        color = Color(0xFF81B622).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 7,
        name = "Hold",
        duration = 60,
        color = Color(0xFFCC5216).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 8,
        name = "Rest",
        duration = 20,
        color = Color(0xFF81B622).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 9,
        name = "Hold",
        duration = 120,
        color = Color(0xFFCC5216).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    )
)

val initial_timer_workout_4 = InTimer(
    name = "10-Min Workout",
    description = """
    Repeat 4x: 
    • Squats — 30 seconds 
    • Knee Pushups — 30 seconds 
    • V-Ups — 30 seconds 
    • Mountain Climbers — 30 seconds 
    • Rest — 30 seconds
    """.trimIndent(),
    link = "https://www.self.com/story/heres-a-quick-10-minute-bodyweight-workout-for-beginners",
    pinned = false,
    type = TimerType.WORKOUT
)

val initial_timer_workout_4_intervals = (1..19).map {
    when (it%5) {
        1 -> Interval(
            timerId = 0,
            position = it,
            name = "Squats",
            duration = 30,
            color = Color(0xFFFBEE0F).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT
        )
        2 -> Interval(
            timerId = 0,
            position = it,
            name = "Knee Pushups",
            duration = 30,
            color = Color(0xFFC3E0E5).string(),
            signal = IntervalSignal.VIBRATION,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT
        )
        3 -> Interval(
            timerId = 0,
            position = it,
            name = "V-Ups",
            duration = 30,
            color = Color(0xFFFFC5D0).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT
        )
        4 -> Interval(
            timerId = 0,
            position = it,
            name = "Mnt Climbers",
            duration = 30,
            color = Color(0xFF3AF7F0).string(),
            signal = IntervalSignal.VIBRATION,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT
        )
        else -> Interval(
            timerId = 0,
            position = it,
            name = "Rest",
            duration = 30,
            color = Color(0xFF81B622).string(),
            signal = IntervalSignal.SILENT,
            sound = IntervalSound.KNUCKLE,
            customSoundUri = "",
            type = IntervalType.DEFAULT
        )
    }
}

val initial_timer_common_1 = InTimer(
    name = "Tooth brushing",
    description = """
    The American Dental Association recommends brushing your teeth twice a day with fluoride toothpaste for two minutes each time. 30 seconds per “quadrant”.
    """.trimIndent(),
    link = "https://en.wikipedia.org/wiki/Tooth_brushing",
    pinned = false,
    type = TimerType.OTHER
)

val initial_timer_common_1_intervals = listOf(
    Interval(
        timerId = 0,
        position = 1,
        name = "Top Right",
        duration = 30,
        color = Color(0xFF81B622).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 2,
        name = "Top Left",
        duration = 30,
        color = Color(0xFF274472).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 3,
        name = "Bott. Left",
        duration = 30,
        color = Color(0xFFB22A80).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 4,
        name = "Bott. Right",
        duration = 30,
        color = Color(0xFF31D1D0).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    )
)

val initial_timer_cook_1 = InTimer(
    name = "Medium Rare inch steak",
    description = """
    • Get your pan, grill or BBQ hot+.
    • Put your room temperature steak on the hot grill.
    • Cook the first side for 5 minutes.
    • Turn your steak.
    • Cook the second side for 4 minutes.
    • Let it rest for a couple of minutes.
    • Serve and devour.
    """.trimIndent(),
    link = "https://steaktimer.com/how-long-to-cook-a-1-inch-steak-medium-rare",
    pinned = false,
    type = TimerType.COOK
)

val initial_timer_cook_1_intervals = listOf(
    Interval(
        timerId = 0,
        position = 1,
        name = "Get Hot",
        duration = 60,
        color = Color(0xFFCC5216).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 2,
        name = "Put Steak",
        duration = 10,
        color = Color(0xFFB637FB).string(),
        signal = IntervalSignal.VIBRATION,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 3,
        name = "First Side",
        duration = 300,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 4,
        name = "Turn Over",
        duration = 10,
        color = Color(0xFFB637FB).string(),
        signal = IntervalSignal.VIBRATION,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 5,
        name = "Second Side",
        duration = 240,
        color = Color(0xFF910C00).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 6,
        name = "Take Away",
        duration = 10,
        color = Color(0xFFB637FB).string(),
        signal = IntervalSignal.VIBRATION,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 7,
        name = "Let it Rest",
        duration = 120,
        color = Color(0xFF274472).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    )
)

val initial_timer_cook_2 = InTimer(
    name = "Soft boiled eggs",
    description = """
    Just bring a pot of water to a boil with enough water to cover the eggs by about an inch. By boiling the water first, it also doesn’t matter which type of pot you use as the eggs only hit the water once it’s boiling.

    Reduce the heat to low and use a skimmer to gently place the eggs in the water. By reducing the heat to low, you’ll prevent the eggs from bouncing around and cracking. Then, turn the heat back up to a boil.
    """.trimIndent(),
    link = "https://downshiftology.com/recipes/perfect-soft-boiled-hard-boiled-eggs/",
    pinned = false,
    type = TimerType.COOK
)

val initial_timer_cook_2_intervals = listOf(
    Interval(
        timerId = 0,
        position = 1,
        name = "Boil water",
        duration = 99,
        color = Color(0xFFFEDE00).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 2,
        name = "Place eggs",
        duration = 15,
        color = Color(0xFF7E1E80).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 3,
        name = "Boil",
        duration = 375,
        color = Color(0xFFFB6090).string(),
        signal = IntervalSignal.SILENT,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    ),
    Interval(
        timerId = 0,
        position = 4,
        name = "Put in cold",
        duration = 15,
        color = Color(0xFF31D1D0).string(),
        signal = IntervalSignal.SOUND,
        sound = IntervalSound.KNUCKLE,
        customSoundUri = "",
        type = IntervalType.DEFAULT
    )
)