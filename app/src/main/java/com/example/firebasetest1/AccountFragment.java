package com.example.firebasetest1;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AccountFragment extends Fragment implements View.OnClickListener{
    View vAccount;
    EditText edt_NoP;
    EditText edt_houseName;
    String houseName;
    String nop;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        vAccount = inflater.inflate(R.layout.fragment_account, container, false);

        Context mContext = this.getContext();

        edt_NoP = vAccount.findViewById(R.id.edt_No_people);
        edt_houseName = vAccount.findViewById(R.id.edt_houseName);
        edt_NoP.setOnClickListener(view -> showNumberPicker(mContext, 1, 10));


        Button btn_submit_changes = vAccount.findViewById(R.id.btn_submit_changes);
        btn_submit_changes.setOnClickListener(this);
        return vAccount;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit_changes:
                houseName = edt_houseName.getText().toString();
                nop = edt_NoP.getText().toString();
                boolean flag = true;
                if (houseName.isEmpty()) {
                    edt_houseName.setError("Please input house name");
                    flag = false;
                }

                if (flag == true) {

                }
                break;
        }
    }

    private void showNumberPicker(Context context, int min, int max) {
        final Dialog dialog = new Dialog(context);
        dialog.setTitle("Number of people");
        dialog.setContentView(R.layout.dialog_number_picker);
        Button btn_set = dialog.findViewById(R.id.btn_set);
        btn_set.setVisibility(View.VISIBLE);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final NumberPicker np = dialog.findViewById(R.id.numberPicker1);

        np.setMaxValue(10);
        np.setMinValue(1);
        btn_set.setOnClickListener(view -> {
            edt_NoP.setText(String.valueOf(np.getValue()));
            dialog.dismiss();
        });
        btn_cancel.setOnClickListener(view -> {
            dialog.dismiss();
        });
        dialog.show();
    }
}
