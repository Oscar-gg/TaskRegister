package com.example.TaskRegister;

// Esta clase esta basada en la realizada en https://www.youtube.com/watch?v=HrZgfoBeams. Todas las
// variables son diferentes y hay una validación diferente. También, los getters son diferentes.
// Lo único que quedó igual es el nombre de la clase y su función en el programa.

public class ListElement {

    private String taskName, dueDate, category, taskID, status;
    private int priority;

    // Esta clase contiene la información que se despliega en cada ViewGroup del RecyclerView.
    public ListElement(String taskName, String dueDate, String category, String taskID,
                       String status, int priority) {

        // Para evitar que los nombres sean demasiado largos.
        if (taskName.length() > 12){
            taskName = taskName.substring(0,12) + "...";
        }

        if (category.length() > 11){
            category = category.substring(0,11) + "...";
        }

        this.taskName = taskName;
        this.dueDate = dueDate;
        this.category = category;
        this.taskID = taskID;
        this.status = status;
        this.priority = priority;
    }

    // Getters

    public String getTaskID() {
        return taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getCategory() {
        return category;
    }

    public String getStatusLE() {
        return status;
    }

    public int getPriorityLE() {
        return priority;
    }

}
