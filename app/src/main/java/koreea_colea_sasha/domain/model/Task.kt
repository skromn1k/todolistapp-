package koreea_colea_sasha.domain.model

data class Task(
    var taskName : String? = null,
    var taskDescription : String? = null,
    var taskDueDate : String? = null,
    var taskIsFinished : Boolean? = null,
    var taskID : String? = null,
)
