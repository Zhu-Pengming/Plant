package com.tom.npm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class InstructionFragment extends Fragment {

    private Button instructionContinue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instruction, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        instructionContinue = view.findViewById(R.id.instruction_continue);
        instructionContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pop InstructionFragment from the back stack
                if (getFragmentManager() != null) {
                    getFragmentManager().popBackStack();
                    getActivity().overridePendingTransition(R.anim.instruction_out, R.anim.instruction_in);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Show other views
        getActivity().findViewById(R.id.surfaceView).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.camera_return).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.btnSwitch).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.btnTakePhoto).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.camera_gallery1).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.camera_gallery2).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.camera_instruction1).setVisibility(View.VISIBLE);
        getActivity().findViewById(R.id.camera_instruction2).setVisibility(View.VISIBLE);
    }
}