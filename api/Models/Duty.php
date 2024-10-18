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

    // Scope to get upcoming duties (status: pending, date: future or time in future)     
    public function scopeUpcoming($query)     
    {         
        return $query->where('status', 'pending')->where(function ($query) {             
            $query->where('date', '>', now()->toDateString())                   
                  ->orWhere(function ($query) {                       
                      $query->where('date', '=', now()->toDateString())                             
                            ->where('start_time', '>', now()->toTimeString());                   
                  });         
        });     
    }      

    // Scope to get completed duties (status: finished or date/time has passed)     
    public function scopeCompleted($query)     
    {         
        return $query->where('status', 'finished')             
            ->orWhere(function ($query) {                 
                // Automatically mark duties as finished if the time has passed                 
                $query->where('status', 'pending')                       
                      ->where(function ($query) {                           
                          $query->where('date', '<', now()->toDateString())                                 
                                ->orWhere(function ($query) {                                     
                                    $query->where('date', '=', now()->toDateString())                                           
                                          ->where('end_time', '<', now()->toTimeString());                                 
                                });                       
                      });             
            });     
    }      

    // Mutator to automatically update status based on date and time     
    public function setStatusAttribute($value)     
    {         
        $now = Carbon::now();                  

        // Check if both date and time are set         
        if (isset($this->attributes['date']) && isset($this->attributes['start_time']) && isset($this->attributes['end_time'])) {             
            $dutyDate = Carbon::parse($this->attributes['date']);             
            $startTime = Carbon::parse($this->attributes['start_time']);             
            $endTime = Carbon::parse($this->attributes['end_time']);             
            $nowTime = $now->copy()->format('H:i:s');                          

            // Check if the duty is in the future             
            if ($dutyDate->isFuture() || ($dutyDate->isToday() && $startTime->gt($nowTime))) {                 
                $this->attributes['status'] = 'pending';             
            }             
            // Check if the duty is pending             
            elseif ($dutyDate->isToday() && $startTime->lte($nowTime) && $endTime->gt($nowTime)) {                 
                $this->attributes['status'] = 'pending';             
            }             
            // Otherwise, it must be finished             
            else {                 
                $this->attributes['status'] = 'finished';             
            }         
        } else {             
            // If no date or time is set, default to the passed value or 'pending'             
            $this->attributes['status'] = $value ?: 'pending';         
        }     
    }      

    // Ensure status is set when creating or updating the duty     
    public static function boot()     
    {         
        parent::boot();          

        // Automatically set status when creating a new Duty         
        static::creating(function ($model) {             
            $model->setStatusAttribute($model->status);         
        });          

        // Automatically set status when updating an existing Duty         
        static::updating(function ($model) {             
            $model->setStatusAttribute($model->status);         
        });     
    } 
}
