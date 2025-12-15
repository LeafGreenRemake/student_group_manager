package com.example.student_group_manager;

import android.app.Dialog;

public class AddClassroomFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?) :Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view: View = inflater.inflate(R.layout.fragment_add_subject, null)
        }
    }
}
