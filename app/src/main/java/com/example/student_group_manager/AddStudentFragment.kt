package com.example.student_group_manager

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.student_group_manager.data.Student
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class AddStudentFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.fragment_add_student, null)

            builder.setView(view)
                .setTitle("Add New Student")
                .setPositiveButton("Save") { _, _ -> }  // Empty listener to prevent auto-dismiss
                .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            val dialog = builder.create()

            dialog.setOnShowListener {
                val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                positiveButton.setOnClickListener {
                    val nameEt = view.findViewById<EditText>(R.id.student_name_et)
                    val name = nameEt.text.toString().trim()

                    if (name.isEmpty()) {
                        Toast.makeText(dialog.context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener  // Don't dismiss
                    }

                    saveStudentToDatabase(name, dialog)
                }
            }

            dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun saveStudentToDatabase(name: String, dialog: Dialog) {
        val subjectId = arguments?.getString("subject_id") ?: run {
            Toast.makeText(dialog.context, "No subject ID provided", Toast.LENGTH_SHORT).show()
            return
        }
        val classroomId = arguments?.getString("classroom_id") ?: run {
            Toast.makeText(dialog.context, "No classroom ID provided", Toast.LENGTH_SHORT).show()
            return
        }

        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database
        val uid = auth.currentUser?.uid ?: return
        val studentsRef = database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectClassrooms").child(classroomId).child("classroomStudents")
        val newStudentRef = studentsRef.push()  // Generates unique key

        val key = newStudentRef.key
        if (key == null) {
            Toast.makeText(dialog.context, "Failed to generate unique ID", Toast.LENGTH_SHORT).show()
            return
        }

        val student = Student(key, name, mapOf())

        newStudentRef.setValue(student)
            .addOnSuccessListener {
                Toast.makeText(dialog.context, "Student added successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()  // Dismiss only after success
            }
            .addOnFailureListener { e ->
                Toast.makeText(dialog.context, "Failed to add student: ${e.message}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()  // Optionally dismiss on failure; adjust if you want to retry
            }
    }

    companion object {
        fun newInstance(subjectId: String, classroomId: String): AddStudentFragment {
            val fragment = AddStudentFragment()
            val args = Bundle()
            args.putString("subject_id", subjectId)
            args.putString("classroom_id", classroomId)
            fragment.arguments = args
            return fragment
        }
    }
}