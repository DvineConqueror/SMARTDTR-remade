<?php

namespace App\Http\Controllers;

use App\Models\Duty;
use Illuminate\Http\Request;

class DutyController extends Controller
{
    // GET all duties with teacher and multiple students
    public function index()
    {
        try {
            // Eager load the teacher and students relationships
            $duties = Duty::with(['teacher', 'students'])->get();

            // Modify the response to include the teacher and all students
            $duties = $duties->map(function ($duty) {
                return [
                    'id' => $duty->id,
                    'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                    'students' => $duty->students->map(function ($student) {
                        return $student->lastname . ', ' . $student->firstname;
                    })->toArray(),
                    'subject' => $duty->subject,
                    'room' => $duty->room,
                    'date' => $duty->date,
                    'start_time' => $duty->start_time,
                    'end_time' => $duty->end_time,
                    'status' => $duty->status,
                ];
            });

            return response()->json($duties, 200);

        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json('Failed to retrieve duties.', 500);
        }
    }

    // CREATE a new duty and attach multiple students to it
    public function store(Request $request)
    {
        try {
            $validatedData = $request->validate([
                'teacher_id' => 'required|exists:teachers,id',
                'student_ids' => 'nullable|array',
                'student_ids.*' => 'exists:students,id', // validate each student ID
                'subject' => 'required|string|max:255',
                'room' => 'required|string|max:255',
                'date' => 'required|date',
                'start_time' => 'required|date_format:H:i',
                'end_time' => 'required|date_format:H:i|after:start_time',
                'status' => 'required|in:pending,finished,canceled',
            ]);

            // Create the duty
            $duty = Duty::create($validatedData);

            // Attach students to the duty
            if (!empty($request->student_ids)) {
                $duty->students()->attach($request->student_ids);
            }

            // Fetch the newly created duty with teacher and students
            $duty = Duty::with(['teacher', 'students'])->find($duty->id);

            // Format the response
            $dutyData = [
                'id' => $duty->id,
                'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                'students' => $duty->students->map(function ($student) {
                    return $student->lastname . ', ' . $student->firstname;
                })->toArray(),
                'subject' => $duty->subject,
                'room' => $duty->room,
                'date' => $duty->date,
                'start_time' => $duty->start_time,
                'end_time' => $duty->end_time,
                'status' => $duty->status,
            ];

            return response()->json($dutyData, 201);

        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json('Failed to create duty.', 500);
        }
    }

    // GET upcoming duties with teacher and multiple students
    public function getUpcomingDuties()
    {
        try {
            $upcomingDuties = Duty::upcoming()->with(['teacher', 'students'])->get();

            $upcomingDuties = $upcomingDuties->map(function ($duty) {
                return [
                    'id' => $duty->id,
                    'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                    'students' => $duty->students->map(function ($student) {
                        return $student->lastname . ', ' . $student->firstname;
                    })->toArray(),
                    'subject' => $duty->subject,
                    'room' => $duty->room,
                    'date' => $duty->date,
                    'start_time' => $duty->start_time,
                    'end_time' => $duty->end_time,
                    'status' => $duty->status,
                ];
            });

            return response()->json($upcomingDuties, 200);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json('Failed to retrieve upcoming duties.', 500);
        }
    }

    // GET completed duties with teacher and multiple students
    public function getCompletedDuties()
    {
        try {
            $completedDuties = Duty::completed()->with(['teacher', 'students'])->get();

            $completedDuties = $completedDuties->map(function ($duty) {
                return [
                    'id' => $duty->id,
                    'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                    'students' => $duty->students->map(function ($student) {
                        return $student->lastname . ', ' . $student->firstname;
                    })->toArray(),
                    'subject' => $duty->subject,
                    'room' => $duty->room,
                    'date' => $duty->date,
                    'start_time' => $duty->start_time,
                    'end_time' => $duty->end_time,
                    'status' => $duty->status,
                ];
            });

            return response()->json($completedDuties, 200);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json('Failed to retrieve completed duties.', 500);
        }
    }

    // UPDATE a duty and sync multiple students
    public function update(Request $request, $id)
    {
        try {
            $duty = Duty::findOrFail($id);

            $validatedData = $request->validate([
                'teacher_id' => 'required|exists:teachers,id',
                'student_ids' => 'nullable|array',
                'student_ids.*' => 'exists:students,id', // validate each student ID
                'subject' => 'required|string|max:255',
                'room' => 'required|string|max:255',
                'date' => 'required|date',
                'start_time' => 'required|date_format:H:i',
                'end_time' => 'required|date_format:H:i|after:start_time',
                'status' => 'required|in:pending,finished,canceled',
            ]);

            // Update duty
            $duty->update($validatedData);

            // Sync students (update the list of students associated with the duty)
            if (!empty($request->student_ids)) {
                $duty->students()->sync($request->student_ids);
            }

            // Fetch updated duty with teacher and students
            $duty = Duty::with(['teacher', 'students'])->find($duty->id);

            $dutyData = [
                'id' => $duty->id,
                'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                'students' => $duty->students->map(function ($student) {
                    return $student->lastname . ', ' . $student->firstname;
                })->toArray(),
                'subject' => $duty->subject,
                'room' => $duty->room,
                'date' => $duty->date,
                'start_time' => $duty->start_time,
                'end_time' => $duty->end_time,
                'status' => $duty->status,
            ];

            return response()->json(['message' => 'Duty updated successfully', 'duty' => $dutyData], 200);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json('Failed to update duty.', 500);
        }
    }

    // DELETE a duty
    public function destroy($id)
    {
        try {
            $duty = Duty::findOrFail($id);
            $duty->delete();

            return response()->json(['message' => 'Duty deleted successfully'], 200);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json('Failed to delete duty.', 500);
        }
    }
}
