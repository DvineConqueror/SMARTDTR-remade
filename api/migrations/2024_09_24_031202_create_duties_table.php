<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateDutiesTable extends Migration
{
    public function up()
    {
        Schema::create('duties', function (Blueprint $table) {
            $table->id();  // Primary key
            $table->foreignId('teacher_id')->constrained('teachers')->onDelete('cascade');  // Foreign key for teachers
            $table->foreignId('student_id')->nullable()->constrained('students')->onDelete('cascade');  // Foreign key for students
            $table->string('subject');  // Duty subject
            $table->string('room');  // Room for the duty
            $table->date('date');  // Date of the duty
            $table->time('start_time');  // Start time of the duty
            $table->time('end_time');  // End time of the duty
            $table->enum('status', ['Pending', 'Finished', 'Canceled'])->default('Pending');  // Status of the duty
            $table->timestamps();  // Created at and updated at timestamps
        });
    }

    public function down()
    {
        Schema::dropIfExists('duties');  // Drop the duties table
    }
}
