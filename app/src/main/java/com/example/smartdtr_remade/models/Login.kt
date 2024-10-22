data class LoginRequest(
    val id: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val userId: String,
    val user: User, // Keep user as it contains detailed info about the logged-in user
)

data class User(
    val userId: String,           // Unique identifier for the user
    val name: String,         // Full name of the user
    val student_id: String?,  // Optional student ID (if applicable)
    val teacher_id: String?,   // Optional teacher ID (if applicable)
    val id: String,
    val firstname: String,
    val lastname: String,
    val email: String,
    val mobile_number: String,
    val sex: String,
    val year_level: String?
)

