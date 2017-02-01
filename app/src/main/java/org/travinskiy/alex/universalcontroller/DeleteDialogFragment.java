package org.travinskiy.alex.universalcontroller;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DeleteDialogFragment extends DialogFragment {

    private static final String VALUE_TO_DELETE = "valueToDelete";

    private String mValue;

    public interface DeleteListener {
        void onConfirm(String value);
    }

    public static DeleteDialogFragment newInstance(String value) {
        DeleteDialogFragment dialogFragment = new DeleteDialogFragment();

        Bundle args = new Bundle();
        args.putString(VALUE_TO_DELETE, value);
        dialogFragment.setArguments(args);

        return dialogFragment;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mValue = getArguments().getString(VALUE_TO_DELETE);

        return new AlertDialog.Builder(getActivity())
                .setMessage(R.string.sure_delete)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((DeleteListener) getActivity()).onConfirm(mValue);
                    }
                })
                .create();
    }
}
