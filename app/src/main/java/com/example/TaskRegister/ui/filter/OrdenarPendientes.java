package com.example.TaskRegister.ui.filter;

import com.example.TaskRegister.Pendientes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class OrdenarPendientes {

    private static int[] toFilter;// Contiene lista de elementos con la seleccion. Le falta ordenar.
    private static int[] filteredList; // Contiene lista filtrada por fecha y prioridad.
    private static int filterIndex; // Indice para el arreglo toFilter.
    private static boolean urgencySelected; // Asignado desde SharedPreferences, modificable en ajustes
    private static HashMap<Integer, Pendientes> selected; // Apuntador al HashMap selecionado.

    // Input: el nombre de la categoria seleccionada y la opcion seleccionada, si son activos o completados
    // output: arreglo de enteros que tienen las posiciones de los pendientes de manera ordenada por
    // fecha y prioridad.

    // Proceso: primero conseguir cuales son las opciones de cada categoria
    // Actualizar estas opciones cada vez que el usuario borra un pendiente, agrega uno o lo actualiza.
    // Despues, conseguir la lista de todos los pendientes que son de la selección,
    // sean completados o no completados
    // Despues, ordenarlos por prioridad y fecha.

    // Las categorias que se pueden seleccionar para filtrar.
    private static final String[] menuCategoriesAvailable = {"Elegir", "Status", "Categoría",
            "Prioridad", "Responsables"};

    // Las opciones de las categorias. Se despliegan dependiendo de la categoría que sea
    // seleccionada. Algunas son siempre iguales, y otras dependen en la información que haya puesto
    // el usuario con anterioridad.

    private static final String[] statusAvailable = {"Seleccionar", "Por iniciar", "En progreso",
            "En espera", "-"};

    private static final String[] prioritiesAvailable = {"Seleccionar", "1", "2", "3", "4", "5",
                                                         "6", "7", "8", "9", "10"};
    // HashSet para solo agregar elementos no duplicados

    private static Set<String> categoriesAvailable = new HashSet<>();
    private static Set<String> responsiblesAvailable = new HashSet<>();

    // Métodos para interactuar con los HashSets

    // Usados en los constructores.
    public static void addCategoriesAvailable(String category) {
        if (category == null || category.equals(""))
            return;
        OrdenarPendientes.categoriesAvailable.add(category);
    }

    public static void addResponsiblesAvailable(String[] responsible) {
        if (responsible == null){
            return;
        }

        for (int x = 0; x<responsible.length;x++){
            if (responsible[x].equals("")){
                continue;       // Para no agregar responsables en blanco.
            }
            OrdenarPendientes.responsiblesAvailable.add(responsible[x]);
        }
    }


    // Lo regresa como String, para que pueda ser usado en los "spinner".
    public static String[] getResponsiblesAvailable() {

        String[] responsibles = new String[responsiblesAvailable.size() + 1];
        responsibles[0] = "Seleccionar"; // Se pone la opción default.
        int index = 1;

        // Añade todos los responsables al arreglo de strings.
        for (String person: OrdenarPendientes.responsiblesAvailable){
            responsibles[index++] = person;
        }
        return responsibles;
    }

    // Lo regresa como String, para que pueda ser usado en los "spinner".
    public static String[] getCategoriesAvailable() {
        String[] categories = new String[categoriesAvailable.size() + 1];
        categories[0] = "Seleccionar"; // Se pone la opcion default.
        int index = 1;

        // Añade todas las categorías al arreglo de strings.
        for (String category: OrdenarPendientes.categoriesAvailable){
            categories[index++] = category;
        }

        return categories;
    }

    // Se usa para determinar cuales opciones se tienen que mostrar a partir del filtro seleccionado.
    public static String[] optionsToShow(String selection){

        switch (selection){
            case "Status":
                return statusAvailable;
            case "Categoría":
                return getCategoriesAvailable();
            case "Prioridad":
                return prioritiesAvailable;
            case "Responsables":
                return getResponsiblesAvailable();
            default:
                return null;
        }
    }


    // Se usa este método para regresar el arreglo con las posiciones de los elementos filtrados.
    // selection: la opción que se ingresa en el segundo spinner.
    // menuCategory: la opción que se selecciona en el primer spinner.
    // filterBy inicializa las siguientes variables: selected, toFilter, filterIndex
    public static void filterBy(int id, String selection, String menuCategory){

        // ID 0 = pendientes activos, no completados; ID 1: pendientes completados, inactivos.
        if (id == 0){
            selected = Pendientes.getActiveTasks();
        } else {
            selected = Pendientes.getCompletedTasks();
        }

        Set entrySet;
        Iterator it;
        Map.Entry<Integer, Pendientes> entry;
        String[] taskData = null;

        // inicializa la lista donde se guardaran los id de los pendientes por orden.
        toFilter = new int[selected.size()];
        filterIndex = 0; // El indice empieza en 0.

        entrySet = selected.entrySet();
        it = entrySet.iterator();

        // Como los responsables se imprimen concatenados por "@@@" en la funcion toString(), estos
        // se tienen que obtener directamente desde el atributo del objeto, para no tener que lidiar
        // adicionalmente con esto.
        if (menuCategory.equals("Responsables")){

            // Añade los pendientes que tengan el responsable que se está buscando
            while(it.hasNext()){
                entry = (Map.Entry<Integer, Pendientes>) it.next();
                if (entry.getValue().getResponsible() == null) continue;
                for (int x = 0; x<entry.getValue().getResponsible().length; x++){
                    if (entry.getValue().getResponsible()[x].equals(selection)){
                        // La llave de un pendiente siempre es igual a su ID.
                        toFilter[filterIndex++] = entry.getKey();
                        break;
                    }
                }
            }
            // No se comprueba que la selección es la default porque esto se hace desde el listener.
        } else {
            // Se hace un loop por el hashmap, para conseguir los pendientes que concuerdan con la busqueda.
            while (it.hasNext()){
                entry = (Map.Entry<Integer, Pendientes>) it.next();

                // Se podría hacer que el programa itere de manera separada por los
                // atributos de cada pendiente en funcion del nombre de la categoría dada,
                // sin embargo, para esto se tendrían que hacer muchos if - else, lo que consistiría
                // en 'hardcode' que haría más díficil agregar nuevos atibutos a los pendientes.

                // Contiene los valores de todos los atributos del pendiente. En formato String.
                taskData = entry.getValue().toString().split(Pendientes.SEPARATOR);

                for (String atributeValue : taskData) {
                    if (atributeValue.equals(selection)) {
                        toFilter[filterIndex++] = entry.getKey();
                        break;
                    }
                }
            }
        }
        finalSorts(id, false); // Se le pasa el id para indicar si son los completados o los activos.
        // La lista con los pendientes en orden es filteredList.
    }

    // Ordena los pendientes por fecha y prioridad, dependiendo de la selección del usuario.
    public static void finalSorts(int id, boolean reset) {

        // Esto hace posible que sea llamada esta función desde más lugares, aparte de
        // la función filterBy();
        if (reset){
            selected = null;
        }

        if (selected == null){
            if (id == 0){
                selected = Pendientes.getActiveTasks();
            } else {
                selected = Pendientes.getCompletedTasks();
            }

            filterIndex = selected.size();
            toFilter = new int[filterIndex];
            initializeToFilterDefault(); // llena toFilter con los indices de las tareas.
        } // Resetea la lista a ordenar por arrangeByPriority() o ByDate()

        // Se filtra con base en la selección del usuario.
        if (urgencySelected){
            arrangeByPriority();
            arrangeByDate();
        } else {
            arrangeByDate();
            arrangeByPriority();
        }
    }

    // Se llama cuando toFilter no está inicializado. Esto sucede cuando se carga el RecyclerView
    // y cuando no se busca un elemento en particular.
    private static void initializeToFilterDefault() {
        Set entrySet = selected.entrySet();
        Iterator it = entrySet.iterator();
        Map.Entry<Integer, Pendientes> entry;
        int x = 0;
        while (it.hasNext()){
            entry = (Map.Entry<Integer, Pendientes>) it.next();
            toFilter[x++] = entry.getKey();
        }

    }

    // Ordena los pendientes por prioridad.
    // Se utiliza el bubble sort para ordenar los pendientes.
    private static void arrangeByPriority() {
        if (selected == null)
            return;
        // selected: referencia al HashMap seleccionado (puede ser de activos o inactivos)
        boolean flagSwap = true;
        // Bubble sort. Ordena los pendientes con la prioridades de menor a mayor. La prioridad 1
        // es la que va primero.

        // Se utiliza el filterIndex porque el tamaño de toFilter es igual al tamaño del número de
        // pendientes.
        for (int x = 0; x < filterIndex - 1 && flagSwap; x++) {
            flagSwap = false;
            // toFilter: Lista de elementos seleccionados (lista original después de filtrar).
            for (int y = 0; y < filterIndex - 1 - x; y++) {
                int a = selected.get(toFilter[y + 1]).getPriority();
                int b = selected.get(toFilter[y]).getPriority();
                if (a < b) {
                    int temp = toFilter[y];
                    toFilter[y] = toFilter[y + 1];
                    toFilter[y + 1] = temp;
                    flagSwap = true;
                }
            }
        }
    }

    // Arregla los pendientes por día. Los más recientes van primero.
    private static void arrangeByDate(){
        if (selected == null)
            return;

        boolean flagSwap = true;
        // Bubble sort. Ordena los pendientes por fecha. Los de fechas más recientes van primero.

        // Se utiliza el filterIndex porque el tamaño de toFilter es igual al tamaño del número de
        // pendientes.
        for (int x = 0; x < filterIndex - 1 && flagSwap; x++) {
            flagSwap = false;

            for (int y = 0; y < filterIndex - 1 - x; y++) {
                int a, b;
                // Consigue un valor númerico que representa el día. Los fechas más recientes van
                // primero.
                a = numericDate(selected.get(toFilter[y+1]).getDueDate());
                b = numericDate(selected.get(toFilter[y]).getDueDate());

                if (a < b) {
                    int temp = toFilter[y];
                    toFilter[y] = toFilter[y + 1];
                    toFilter[y + 1] = temp;
                    flagSwap = true;
                }
            }
        }
    }

    public static int numericDate(String date){

        if (date.equals("-")) {
            // Esto hace que los que no tienen fecha siempre se vayan al final.
            return  37000;
        }

        String[] dateSeparated;
        int numberDate;

        dateSeparated = date.split("/");
        // Consigue un valor númerico que representa la fecha
        // Se multiplica por 32 porque un mes mayor siempre irá después y de esta manera se evita
        // el tener que multiplicar por 31 o menos días en meses específicos.
        numberDate = Integer.parseInt(dateSeparated[2]) * 365 +
                     Integer.parseInt(dateSeparated[1]) * 32 +
                     Integer.parseInt(dateSeparated[0]);
        return numberDate;
    }

    // Getters y setters

    // Regresa la lista pero sin los espacios en blanco que tiene.
    public static int[] getFilteredList() {
        filteredList = new int[filterIndex];
       for (int x = 0; x < filterIndex; x++){
           filteredList[x] = toFilter[x];
       }
        return filteredList;
    }

    public static void setUrgencySelected(boolean urgencySelected) {
        OrdenarPendientes.urgencySelected = urgencySelected;
    }

    public static boolean isUrgencySelected() {
        return urgencySelected;
    }

    public static String[] getMenuCategoriesAvailable() {
        return menuCategoriesAvailable;
    }

    public static String[] getPrioritiesAvailable() {
        return prioritiesAvailable;
    }

    public static String[] getStatusAvailable() {
        return statusAvailable;
    }
}
