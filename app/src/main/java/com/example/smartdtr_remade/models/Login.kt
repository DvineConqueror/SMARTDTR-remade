data class LoginRequest(
    val id: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val name: String,
    val student_id: String,
    val teacher_id: String
)
