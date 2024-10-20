<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use App\Models\Student;
use App\Models\Teacher;
use Laravel\Sanctum\PersonalAccessToken;

class AuthController extends Controller
{
    // Login method
    public function login(Request $request)
    {
        $id = $request->input('id');
        $password = $request->input('password');

        // Determine if the user is a student or teacher based on ID prefix
        if (str_starts_with($id, 'S')) {
            $user = Student::where('student_id', $id)->first();
        } elseif (str_starts_with($id, 'T')) {
            $user = Teacher::where('teacher_id', $id)->first();
        } else {
            return response()->json(['error' => 'Invalid ID'], 400);
        }

        // Check if user exists and password is correct
        if ($user && Hash::check($password, $user->password)) {
            // Create and return the token
            $token = $user->createToken('loginToken')->plainTextToken;
            return response()->json([
                'token' => $token,
                'user' => $user,
                'userId' => $user->id, // Directly access the ID
            ], 200);
        } else {
            return response()->json(['error' => 'Invalid credentials'], 401);
        }
    }

    // Signup method
    public function signup(Request $request)
    {
        try {
            // Extract the ID to determine whether it's a student or teacher
            $id = $request->input('ID');  // Assuming ID field is used to identify students and teachers
            $isStudent = str_starts_with($id, 'S');
            $isTeacher = str_starts_with($id, 'T');
    
            // Common validation for both students and teachers
            $rules = [
                'firstname' => 'required|string|max:255',
                'lastname' => 'required|string|max:255',
                'mobile_number' => 'required|string|max:11',
                'sex' => 'required|in:Male,Female',
            ];
    
            // Add specific validation based on whether the user is a student or teacher
            if ($isStudent) {
                $rules['year_level'] = 'required|integer|min:1|max:6';  // Validation for year level
                $rules['ID'] = 'required|unique:students,student_id';
            } elseif ($isTeacher) {
                $rules['email'] = 'required|email|unique:teachers,email';
                $rules['ID'] = 'required|unique:teachers,teacher_id';
            } else {
                return response()->json(['message' => 'Invalid ID format.'], 400);
            }
    
            // Validate the request based on the rules
            $request->validate($rules);
    
            // Ensure password is present and valid
            if (!$request->has('password') || strlen($request->password) < 8) {
                return response()->json(['message' => 'Password must be at least 8 characters long.'], 400);
            }
    
            // Create the appropriate user (Student or Teacher)
            $user = null;
    
            if ($isStudent) {
                // Create a new student
                $user = Student::create([
                    'student_id' => $id,
                    'firstname' => $request->firstname,
                    'lastname' => $request->lastname,
                    'email' => $request->email,
                    'password' => Hash::make($request->password),
                    'mobile_number' => $request->mobile_number,
                    'year_level' => $request->year_level,
                    'sex' => $request->sex,
                ]);
            } elseif ($isTeacher) {
                // Create a new teacher
                $user = Teacher::create([
                    'teacher_id' => $id,
                    'firstname' => $request->firstname,
                    'lastname' => $request->lastname,
                    'email' => $request->email,
                    'password' => Hash::make($request->password),
                    'mobile_number' => $request->mobile_number,
                    'sex' => $request->sex,
                ]);
            }
    
            // Check if the user was successfully created
            if ($user) {
                \Log::info('User created successfully: ', ['user_id' => $user->id]);
    
                // Generate a token for the user
                $token = $user->createToken('auth_token', ['*'])->plainTextToken;
    
                return response()->json([
                    'message' => 'user created successfully',
                    'token' => $token,
                    'user' => $user,
                    'userId' => $user->id,
                ], 201);
            } else {
                \Log::error('Failed to create user: ', ['request' => $request->all()]);
                return response()->json(['message' => 'Failed to create user.'], 500);
            }
    
        } catch (\Exception $e) {
            // Catch any exceptions and log the error
            \Log::error('Error occurred while creating user: ' . $e->getMessage());
    
            return response()->json([
                'message' => 'An error occurred while creating the user.',
                'error' => $e->getMessage(),
            ], 500);
        }
    }
    



    // Logout method
    public function changePassword(Request $request)
    {
        // Validate the request data
        $request->validate([
            'userId' => 'required', // Ensure userId is provided
            'old_password' => 'required',
            'new_password' => 'required|min:8|confirmed', // Use 'confirmed' to check new_password_confirmation
        ]);
    
        // Check if the user_id belongs to a teacher
        $user = Teacher::where('teacher_id', $request->userId)->first();
    
        // If not a teacher, check if it's a student
        if (!$user) {
            $user = Student::where('student_id', $request->userId)->first();
        }
    
        // If the user is not found, return an error
        if (!$user) {
            return response()->json(['message' => 'User not found'], 404);
        }
    
        // Check if the old password matches the current password
        if (!Hash::check($request->old_password, $user->password)) {
            return response()->json(['message' => 'Old password is incorrect'], 400);
        }
    
        // Update the password to the new password
        $user->password = Hash::make($request->new_password);
        $user->save();
    
        // Return a success response
        return response()->json(['message' => 'Password changed successfully'], 200);
    }
    
    
}



