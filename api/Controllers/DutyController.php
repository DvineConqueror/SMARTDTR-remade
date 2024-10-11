<?php

namespace App\Http\Controllers;

use App\Models\Duty;
use Illuminate\Http\Request;

class DutyController extends Controller
{

    // GET all duties with teacher and student names
    public function index()
    {
        try {
            // Eager load the teacher and student relationships
            $duties = Duty::with(['teacher', 'student'])->get();

            // Modify the response to include the teacher and student full names
            $duties = $duties->map(function ($duty) {
                return [
                    'id' => $duty->id,
                    'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                    'student_name' => $duty->student ? $duty->student->lastname . ', ' . $duty->student->firstname : 'N/A',
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

    // CREATE a new duty and upload it to the database
    public function store(Request $request)
    {
        try {
            $validatedData = $request->validate([
                'teacher_id' => 'required|exists:teachers,id',
                'student_id' => 'nullable|exists:students,id',
                'subject' => 'required|string|max:255',
                'room' => 'required|string|max:255',
                'date' => 'required|date',
                'start_time' => 'required|date_format:H:i',
                'end_time' => 'required|date_format:H:i|after:start_time',
                'status' => 'required|in:pending,finished,canceled',
            ]);

            // Attempt to create the duty
            $duty = Duty::create($validatedData);

            // Eager load the newly created duty with teacher and student names
            $duty = Duty::with(['teacher', 'student'])->find($duty->id);

            // Format the response with teacher and student names
            $dutyData = [
                'id' => $duty->id,
                'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                'student_name' => $duty->student ? $duty->student->lastname . ', ' . $duty->student->firstname : 'N/A',
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

    // GET upcoming duties with teacher and student names
    public function getUpcomingDuties()
    {
        try {
            $upcomingDuties = Duty::upcoming()->with(['teacher', 'student'])->get();

            $upcomingDuties = $upcomingDuties->map(function ($duty) {
                return [
                    'id' => $duty->id,
                    'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                    'student_name' => $duty->student ? $duty->student->lastname . ', ' . $duty->student->firstname : 'N/A',
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

    // GET completed duties with teacher and student names
    public function getCompletedDuties()
    {
        try {
            $completedDuties = Duty::completed()->with(['teacher', 'student'])->get();

            $completedDuties = $completedDuties->map(function ($duty) {
                return [
                    'id' => $duty->id,
                    'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                    'student_name' => $duty->student ? $duty->student->lastname . ', ' . $duty->student->firstname : 'N/A',
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

    // UPDATE a duty
    public function update(Request $request, $id)
    {
        try {
            $duty = Duty::findOrFail($id);

            $validatedData = $request->validate([
                'teacher_id' => 'required|exists:teachers,id',
                'student_id' => 'nullable|exists:students,id',
                'subject' => 'required|string|max:255',
                'room' => 'required|string|max:255',
                'date' => 'required|date',
                'start_time' => 'required|date_format:H:i',
                'end_time' => 'required|date_format:H:i|after:start_time',
                'status' => 'required|in:pending,finished,canceled',
            ]);

            $duty->update($validatedData);

            // Fetch updated duty with teacher and student names
            $duty = Duty::with(['teacher', 'student'])->find($duty->id);

            $dutyData = [
                'id' => $duty->id,
                'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                'student_name' => $duty->student ? $duty->student->lastname . ', ' . $duty->student->firstname : 'N/A',
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
