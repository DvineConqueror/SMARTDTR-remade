<?php

namespace App\Http\Controllers;

use Illuminate\Support\Facades\DB; // Import DB
use Illuminate\Support\Facades\Log; // Import Log
use App\Models\Duty;
use App\Models\Student;
use Illuminate\Http\Request;
use Illuminate\Http\Response;

class DutyController extends Controller
{

    public function getDuty($id)
    {
        $duty = Duty::with('students')->find($id); // Assuming 'students' is the relationship defined in Duty model
        return response()->json($duty);
    }

    public function getStudentsFromDuty($id)
    {
        try {
            // Fetch the duty by ID and include the students relationship
            $duty = Duty::with('students')->findOrFail($id);

            // Get the student IDs
            $studentIds = $duty->students->pluck('id')->toArray();

            // Fetch students based on the IDs
            $students = Student::whereIn('id', $studentIds)->get();

            return response()->json($students, 200);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json(['error' => 'Failed to retrieve students for the duty.'], 500);
        }
    }


    public function show($id)
    {
        try {
            $duty = Duty::with(['teacher', 'students'])->find($id);

            // Check if duty exists
            if (!$duty) {
                return response()->json(['message' => 'Duty not found'], 404);
            }

            // Prepare the response data
            $response = [
                'id' => $duty->id,
                'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                'student_ids' => $duty->students->pluck('id')->toArray(), // Get student IDs
                'subject' => $duty->subject,
                'room' => $duty->room,
                'date' => $duty->date,
                'start_time' => $duty->start_time,
                'end_time' => $duty->end_time,
                'status' => $duty->status,
            ];

            return response()->json($response, 200);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json(['message' => 'Failed to retrieve duty details.'], 500);
        }
    }

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
                        return $student->id;
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
                'status' => 'required|in:Pending,Finished,Canceled',
            ]);

            // Create the duty
            $duty = Duty::create($validatedData);

            if (!empty($request->student_ids)) {
                $duty->students()->attach($request->student_ids);
                \Log::info('Attached student IDs:', $request->student_ids);
            }

            // Fetch the newly created duty with teacher and students
            $duty = Duty::with(['teacher', 'students'])->find($duty->id);

            // Format the response
            $dutyData = [
                'id' => $duty->id,
                'teacher_name' => $duty->teacher ? $duty->teacher->lastname . ', ' . $duty->teacher->firstname : 'N/A',
                'students' => $duty->students->map(function ($student) {
                        return $student->id;
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
                        return $student->id;
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
                        return $student->id;
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

   // GET upcoming duties for a specific teacher
    public function getUpcomingDutiesForTeachers(Request $request)
    {
        try {
            $teacherId = $request->query('teacher_id'); // Get the teacher ID from the request

            // Log the received teacher ID
            \Log::info("Received teacher ID: " . $teacherId);

            if (empty($teacherId)) {
                return response()->json(['error' => 'Teacher ID is required'], 400);
            }

            // Fetch upcoming duties for the teacher with eager loading of the students relationship
            $duties = Duty::with('students') // Eager load the students relationship
                ->upcoming()
                ->where('teacher_id', $teacherId)
                ->get();

            // Optionally log the retrieved duties for debugging
            \Log::info("Fetched upcoming duties for teacher: ", $duties->toArray());

            // Map the duties to include teacher name in "LastName, FirstName" format
            $mappedDuties = $duties->map(function ($duty) {
                return [
                    'id' => $duty->id,
                    'teacher_name' => $duty->teacher ? "{$duty->teacher->lastname}, {$duty->teacher->firstname}" : 'N/A',
                    'students' => $duty->students->map(function ($student) {
                        return $student->id;
                    })->toArray(),
                    'subject' => $duty->subject,
                    'room' => $duty->room,
                    'date' => $duty->date,
                    'start_time' => $duty->start_time,
                    'end_time' => $duty->end_time,
                    'status' => $duty->status,
                ];
            });

            return response()->json($mappedDuties, 200);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json(['error' => 'Failed to retrieve upcoming duties for teacher.', 'details' => $e->getMessage()], 500);
        }
    }

    // GET completed duties for a specific teacher
    public function getCompletedDutiesForTeachers(Request $request)
    {
        try {
            $teacherId = $request->query('teacher_id');

            // Log the received teacher ID
            \Log::info("Received teacher ID: " . $teacherId);

            if (empty($teacherId)) {
                return response()->json(['error' => 'Teacher ID is required'], 400);
            }

            // Fetch completed duties for the teacher with eager loading of the students relationship
            $duties = Duty::with('students') // Eager load the students relationship
                ->completed()
                ->where('teacher_id', $teacherId)
                ->get();

            // Optionally log the retrieved duties for debugging
            \Log::info("Fetched completed duties for teacher: ", $duties->toArray());

            // Map the duties to include teacher name in "LastName, FirstName" format
            $mappedDuties = $duties->map(function ($duty) {
                return [
                    'id' => $duty->id,
                    'teacher_name' => $duty->teacher ? "{$duty->teacher->lastname}, {$duty->teacher->firstname}" : 'N/A',
                    'students' => $duty->students->map(function ($student) {
                        return $student->id;
                    })->toArray(),
                    'subject' => $duty->subject,
                    'room' => $duty->room,
                    'date' => $duty->date,
                    'start_time' => $duty->start_time,
                    'end_time' => $duty->end_time,
                    'status' => $duty->status,
                ];
            });

            return response()->json($mappedDuties, 200);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json(['error' => 'Failed to retrieve completed duties for teacher.', 'details' => $e->getMessage()], 500);
        }
    }

    // GET completed duties for a specific student
    public function getCompletedDutiesForStudents(Request $request)
    {
        try {
            $studentId = $request->query('student_id');

            // Log the received student ID
            \Log::info("Received student ID: " . $studentId);

            if (empty($studentId)) {
                return response()->json(['error' => 'Student ID is required'], 400);
            }

            // Fetch completed duties for the student with eager loading of the teacher relationship
            $duties = Duty::with('teacher') // Eager load the teacher relationship
                ->completed()
                ->whereExists(function ($query) use ($studentId) {
                    $query->select(DB::raw(1)) // Use raw 1 to check existence
                        ->from('duty_student')
                        ->join('students', 'students.id', '=', 'duty_student.student_id')
                        ->where('duty_student.duty_id', '=', DB::raw('duties.id')) // Fully qualify 'duties.id'
                        ->where('students.id', '=', $studentId); // Specify the student ID correctly
                })
                ->get();

            // Optionally log the retrieved duties for debugging
            \Log::info("Fetched completed duties for student: ", $duties->toArray());

            // Map the duties to include teacher name in "LastName, FirstName" format
            $mappedDuties = $duties->map(function ($duty) {
                return [
                    'id' => $duty->id,
                    'teacher_name' => $duty->teacher ? "{$duty->teacher->lastname}, {$duty->teacher->firstname}" : 'N/A',
                    'subject' => $duty->subject,
                    'room' => $duty->room,
                    'date' => $duty->date,
                    'start_time' => $duty->start_time,
                    'end_time' => $duty->end_time,
                    'status' => $duty->status,
                ];
            });

            return response()->json($mappedDuties, 200);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json(['error' => 'Failed to retrieve completed duties for student.', 'details' => $e->getMessage()], 500);
        }
    }


    // GET upcoming duties for a specific student
    public function getUpcomingDutiesForStudents(Request $request)
    {
        try {
            $studentId = $request->query('student_id');

            // Log the received student ID
            \Log::info("Received student ID: " . $studentId);

            if (empty($studentId)) {
                return response()->json(['error' => 'Student ID is required'], 400);
            }

            // Fetch upcoming duties for the student with eager loading of the teacher relationship
            $duties = Duty::with('teacher') // Eager load the teacher relationship
                ->upcoming()
                ->whereExists(function ($query) use ($studentId) {
                    $query->select(DB::raw(1)) // Use raw 1 to check existence
                        ->from('duty_student')
                        ->join('students', 'students.id', '=', 'duty_student.student_id')
                        ->where('duty_student.duty_id', '=', DB::raw('duties.id')) // Fully qualify 'duties.id'
                        ->where('students.id', '=', $studentId); // Specify the student ID correctly
                })
                ->get();

            // Log the retrieved duties for debugging
            \Log::info("Fetched duties: ", $duties->toArray());

            // Map the duties to include the teacher name in "LastName, FirstName" format
            $mappedDuties = $duties->map(function ($duty) {
                if ($duty->teacher) {
                    // Ensure the teacher's attributes are accessible
                    $teacherFullName = "{$duty->teacher->lastname}, {$duty->teacher->firstname}";
                } else {
                    $teacherFullName = 'N/A'; // Default if no teacher is associated
                }

                return [
                    'id' => $duty->id,
                    'teacher_name' => $teacherFullName, // Format teacher's name
                    'student_id' => $duty->student_id,
                    'subject' => $duty->subject,
                    'room' => $duty->room,
                    'date' => $duty->date,
                    'start_time' => $duty->start_time,
                    'end_time' => $duty->end_time,
                    'status' => $duty->status,
                ];
            });

            return response()->json($mappedDuties);
        } catch (\Exception $e) {
            \Log::error($e->getMessage());
            return response()->json(['error' => 'Failed to retrieve upcoming duties for student.', 'details' => $e->getMessage()], 500);
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
                        return $student->id;
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