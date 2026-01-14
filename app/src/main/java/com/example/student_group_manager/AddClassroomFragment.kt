package com.example.student_group_manager

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.student_group_manager.data.Classroom
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class AddClassroomFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { activity ->
            val builder = AlertDialog.Builder(activity)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.fragment_add_classroom, null)

            builder.setView(view)
                .setTitle("Add New Classroom")
                .setPositiveButton("Save") {_, _ ->}
                .setNegativeButton("Cancel") {dialog, _ -> dialog.cancel() }

                    

                .setPositiveButton("Save") { dialog, id ->
                    val nameEt = view.findViewById<EditText>(R.id.classroom_name_et)

                    val name = nameEt.text.toString().trim()

                    if (name.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    saveClassroomToDatabase(name)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun saveClassroomToDatabase(name: String) {
        val subjectId = arguments?.getString("subject_id") ?: run {
            Toast.makeText(requireContext(), "No subject ID provided", Toast.LENGTH_SHORT).show()
            return
        }

        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database
        val uid = auth.currentUser?.uid ?: return
        val classroomsRef = database.getReference("teachers").child(uid).child("subjects").child(subjectId).child("subjectClassrooms")
        val newClassroomRef = classroomsRef.push()  // Generates unique key

        val key = newClassroomRef.key
        if (key == null) {
            Toast.makeText(requireContext(), "Failed to generate unique ID", Toast.LENGTH_SHORT).show()
            return
        }

        val classroom = Classroom(key, uid, subjectId, name, mapOf(), mapOf())

        newClassroomRef.setValue(classroom)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Classroom added successfully", Toast.LENGTH_SHORT).show()
                // RecyclerView will auto-update via the ValueEventListener in ClassroomsScreenActivity
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to add classroom: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun newInstance(subjectId: String): AddClassroomFragment {
            val fragment = AddClassroomFragment()
            val args = Bundle()
            args.putString("subject_id", subjectId)
            fragment.arguments = args
            return fragment
        }
    }
}