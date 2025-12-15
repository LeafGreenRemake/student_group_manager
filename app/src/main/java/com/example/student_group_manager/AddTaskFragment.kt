package com.example.student_group_manager

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.student_group_manager.data.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class AddTaskFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.fragment_add_task, null)

            builder.setView(view)
                .setTitle("Add New Task")
                .setPositiveButton("Save") { dialog, id ->
                    val nameEt = view.findViewById<EditText>(R.id.task_name_et)
                    val descEt = view.findViewById<EditText>(R.id.description_et)

                    val name = nameEt.text.toString().trim()
                    val desc = descEt.text.toString().trim()

                    if (name.isEmpty() || desc.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    saveTaskToDatabase(name, desc)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun saveTaskToDatabase(name: String, desc: String) {
        val subjectId = arguments?.getString("subject_id") ?: run {
            Toast.makeText(requireContext(), "No subject ID provided", Toast.LENGTH_SHORT).show()
            return
        }

        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database
        val uid = auth.currentUser?.uid ?: return
        val tasksRef = database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectTasks")
        val newTaskRef = tasksRef.push()  // Generates unique key

        val key = newTaskRef.key
        if (key == null) {
            Toast.makeText(requireContext(), "Failed to generate unique ID", Toast.LENGTH_SHORT).show()
            return
        }

        val task = Task(key, name, desc)

        newTaskRef.setValue(task)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_SHORT).show()

            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to add task: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun newInstance(subjectId: String): AddTaskFragment {
            val fragment = AddTaskFragment()
            val args = Bundle()
            args.putString("subject_id", subjectId)
            fragment.arguments = args
            return fragment
        }
    }
}