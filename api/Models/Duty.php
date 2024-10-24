<?php  
namespace App\Models;  

use Illuminate\Database\Eloquent\Factories\HasFactory; 
use Illuminate\Database\Eloquent\Model; 
use Carbon\Carbon;  

class Duty extends Model 
{     
    use HasFactory;      

    protected $fillable = [         
        'teacher_id',         
        'subject',         
        'room',         
        'date',         
        'start_time',         
        'end_time',         
        'status',     
    ];      

    // Relationship to other tables
    // A Duty belongs to a Teacher     
    public function teacher()     
    {         
        return $this->belongsTo(Teacher::class, 'teacher_id');     
    }      

    // A Duty can have many Students (many-to-many relationship)
    public function students()     
    {         
        return $this->belongsToMany(Student::class);     
    }      

    public function getTeacherNameAttribute()     
    {         
        return $this->teacher ? $this->teacher->last_name . ', ' . $this->teacher->first_name : 'N/A';     
    }
    // Accessor to get all student names as a comma-separated string     
    public function getStudentNamesAttribute()     
    {         
        return $this->students->pluck('last_name', 'first_name')->map(function($last, $first) {
            return $last . ', ' . $first;
        })->implode(', ');     
    }      

    // Scope to get upcoming duties (status: pending)     
    public function scopeUpcoming($query)     
    {         
        return $query->where('status', 'Pending');     
    }      

    // Scope to get completed duties (status: finished)     
    public function scopeCompleted($query)     
    {         
        return $query->where('status', 'Finished');                  
    }      

}