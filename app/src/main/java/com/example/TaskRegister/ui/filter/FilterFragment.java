package com.example.TaskRegister.ui.filter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.TaskRegister.R;

// El propósito de esta clase es mostrar los filtros que están disponibles para el usuario. Desde
// aquí no se pueden modificar, solo visualizar. El propósito de esto, en parte, es que el usario
// pueda ver que efectivamente se guardan las nuevas opciones que pone.

// Las líneas que tienen *Android Def o <Android Def> </Android Def> son las que fueron generadas
// por Android Studio. Total: 4 líneas aproximadamente.

public class FilterFragment extends Fragment {

    // <Android Def>
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_filters, container, false);
    }

    public void onResume(){
        super.onResume();
        // </Android Def>
        initializeTextViews(getView());
    }

    // Pone la información en los TextViews
    private void initializeTextViews(View view) {
        TextView status, categories, priorities, responsibles;
        status = view.findViewById(R.id.filters_tv1);
        categories = view.findViewById(R.id.filters_tv2);
        priorities = view.findViewById(R.id.filters_tv3);
        responsibles = view.findViewById(R.id.filters_tv4);

        status.setText(arrayToString(OrdenarPendientes.getStatusAvailable()));
        categories.setText(arrayToString(OrdenarPendientes.getCategoriesAvailable()));
        priorities.setText(arrayToString(OrdenarPendientes.getPrioritiesAvailable()));
        responsibles.setText(arrayToString(OrdenarPendientes.getResponsiblesAvailable()));

    }

    // Convierte los arreglos a Strings, para que se puedan colocar en la pantalla.
    private String arrayToString(String[] array){
        String text = "";
        for (String item: array){
            // Hace que no se agreguen los "Seleccionar"
            if (item.equals("Seleccionar"))
                continue;
            text += item + ", ";
        }

        if (text.equals(""))
            return "Nada por el momento.";

        // Quita el último ", " y le pone un punto final.
        return text.substring(0,text.length()-2) + ".";
    }

}