package com.example.student_group_manager

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.student_group_manager.data.Subject
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class AddSubjectFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.fragment_add_subject, null)

            builder.setView(view)
                .setTitle("Add New Subject")
                .setPositiveButton("Save") { dialog, id ->
                    val nameEt = view.findViewById<EditText>(R.id.subject_name_et)
                    val descEt = view.findViewById<EditText>(R.id.description_et)

                    val name = nameEt.text.toString().trim()
                    val desc = descEt.text.toString().trim()

                    if (name.isEmpty() || desc.isEmpty()) {
                        Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    saveSubjectToDatabase(name, desc)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun saveSubjectToDatabase(name: String, desc: String) {
        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database
        val uid = auth.currentUser?.uid ?: return
        val subjectsRef = database.getReference("teachers").child(uid).child("subjects")
        val newSubjectRef = subjectsRef.push()  // Generates unique key

        val key = newSubjectRef.key
        if (key == null) {
            Toast.makeText(requireContext(), "Failed to generate unique ID", Toast.LENGTH_SHORT).show()
            return
        }

        val subject = Subject(key, name, desc, mutableListOf(), mutableListOf())

        newSubjectRef.setValue(subject)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Subject added successfully", Toast.LENGTH_SHORT).show()
                // RecyclerView will auto-update via the ValueEventListener in SubjectsActivity
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to add subject: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun newInstance(): AddSubjectFragment = AddSubjectFragment()
    }
}