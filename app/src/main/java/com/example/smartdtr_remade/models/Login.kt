data class LoginRequest(
    val id: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User // Keep user as it contains detailed info about the logged-in user
)

data class User(
    val id: String,           // Unique identifier for the user
    val name: String,         // Full name of the user
    val student_id: String?,  // Optional student ID (if applicable)
    val teacher_id: String?   // Optional teacher ID (if applicable)
)
