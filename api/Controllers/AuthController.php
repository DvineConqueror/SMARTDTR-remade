<?php

namespace App\Http\Controllers;

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash;
use App\Models\Student; // Assuming Student model exists
use App\Models\Teacher; // Assuming Teacher model exists;

class AuthController extends Controller
{
    // Login method as previously defined
    public function login(Request $request)
    {
        $id = $request->input('id');
        $password = $request->input('password');

        // Determine the database based on ID prefix
        if (str_starts_with($id, 'S')) {
            // Query the Student database
            $user = Student::where('id', $id)->first();
        } elseif (str_starts_with($id, 'T')) {
            // Query the Teacher database
            $user = Teacher::where('id', $id)->first();
        } else {
            return response()->json(['error' => 'Invalid ID'], 400);
        }

        // Check if user exists and password is correct
        if ($user && Hash::check($password, $user->password)) {
            // Create authentication token using Sanctum
            $token = $user->createToken('loginToken')->plainTextToken;

            return response()->json(['token' => $token, 'user' => $user], 200);
        } else {
            return response()->json(['error' => 'Invalid credentials'], 401);
        }
    }

    // Signup method
    public function signup(Request $request)
    {
        $id = $request->input('id');
        $name = $request->input('name');
        $email = $request->input('email');
        $password = $request->input('password');
        
        // Validate input
        $request->validate([
            'id' => 'required|unique:students,id|unique:teachers,id',
            'name' => 'required|string|max:255',
            'email' => 'required|email|unique:students,email|unique:teachers,email',
            'password' => 'required|string|min:8',
        ]);

        // Determine if the user is a student or teacher based on ID prefix
        if (str_starts_with($id, 'S')) {
            // Register as Student
            $user = new Student();
        } elseif (str_starts_with($id, 'T')) {
            // Register as Teacher
            $user = new Teacher();
        } else {
            return response()->json(['error' => 'Invalid ID'], 400);
        }

        // Set user details
        $user->id = $id;
        $user->name = $name;
        $user->email = $email;
        $user->password = Hash::make($password);  // Hash the password

        // Save the user to the database
        $user->save();

        // Create authentication token using Sanctum
        $token = $user->createToken('signupToken')->plainTextToken;

        return response()->json(['token' => $token, 'user' => $user], 201);
    }
}
