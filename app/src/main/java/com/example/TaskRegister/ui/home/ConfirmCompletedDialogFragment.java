package com.example.TaskRegister.ui.home;

// Clase utilizada para construir el diálogo que se muestra cuando el usuario presiona un botón.
// En los ajustes (dentro de la app) el usuario puede activar y desactivar la opción de que haya
// confirmaciones al completar un pendiente.

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.TaskRegister.Pendientes;
import com.example.TaskRegister.R;

public class ConfirmCompletedDialogFragment extends DialogFragment {

    DialogListener dialogListener;
    Pendientes activeTask;

    public ConfirmCompletedDialogFragment(Pendientes task, DialogListener dialogListener){
        this.activeTask = task;
        this.dialogListener = dialogListener;
    }

        public Dialog onCreateDialog(Bundle savedInstanceState){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            String message = getResources().getString(R.string.dialog_confirm_message1) + " ";
            message += "'" +activeTask.getName() + "' ";
            message += getResources().getString(R.string.dialog_confirm_message2);

            // Se le ponen los valores al título y mensaje del diálogo de alerta.
            builder.setTitle(R.string.dialog_confirm_title)
                    .setMessage(message);

            // Se agrega un botón, se le pone un listener.
            builder.setPositiveButton(R.string.dialog_positive_message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialogListener.onDialogPositiveClick(activeTask);
                }
            });

            // Se agrega el botón de cancelar. Hace que se cierre el diálogo si se presiona.
            builder.setNegativeButton(R.string.dialog_negative_message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Cierra la ventana de diálogo.
                    ConfirmCompletedDialogFragment.this.getDialog().cancel();
                }
            });

            return builder.create();
        }

        public interface DialogListener{
            void onDialogPositiveClick(Pendientes task);
        }
}
