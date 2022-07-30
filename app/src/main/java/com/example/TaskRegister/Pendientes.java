package com.example.TaskRegister;

import androidx.annotation.Nullable;

import com.example.TaskRegister.ui.filter.OrdenarPendientes;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

// Clase utilizada para modelar los pendientes. También se usa para conseguir la información de los
// pendientes y para guardarla en archivos de texto.

public class Pendientes{

    // Sección 1: atributos

    // Atributos estáticos
    private static int numberOfTasks = 0;
    // Contienen la info que se usará para los spinners (opciones desplegables).
    private static final String[] optionsPriority = {"##", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static final String[] statusOptions = {"Seleccionar", "Por iniciar", "En progreso", "En espera",
            "Terminado", "-"};

    // Es lo que se utilizará para separar los valores de los atributos en los archivos de texto.
    // Antes se utilizaban "," pero esto traía la desventaja de errores en caso de que el usuario
    // puisera una "," en el nombre, descripción, o categoría.
    public static final String SEPARATOR = ",\t@\t";

    // Para guardar todos los pendientes y que se pueda acceder a ellos desde las otras clases
    private static HashMap<Integer, Pendientes> activeTasks = new HashMap<Integer, Pendientes>();
    private static HashMap<Integer, Pendientes> completedTasks = new HashMap<Integer, Pendientes>();

    // Atributos de instancia, de cada pendiente
    private String name;
    private String description;
    private String status;
    private boolean completed;
    private int priority;
    private String[] responsible;
    private String dateCreated;
    private String lastEdit;
    private String dueDate;
    private String category;
    private int taskID;


    // Sección 2: Métodos

    // Métodos Constructores

    // Método para crear los pendientes. Se va a utilizar solo desde la actividad en donde se añaden
    // los pendientes. Se va a comprobar que la información esté correcta desde dicha actividad,
    // puesto que de esta manera se podrían enviar mensajes al usuario mediante el widget "Toast" en
    // caso de que le falte ingresar un dato.

    public Pendientes(String name, String description, String status, int priority,
                      @Nullable String[] responsible, String dueDate, String category,
                      String dateCreated){
        this.name = name;
        this.priority = priority;
        this.dateCreated = dateCreated;
        this.lastEdit = dateCreated;
        this.taskID = numberOfTasks++;

        if (description.equals("")){
            this.description = "Sin descripción. Intenta agregar una.";
        } else {
            this.description = description;
        }

        if (status.equals("Seleccionar")){
            this.status = "-";
        } else{
            this.status = status;
        }

        if (responsible == null){
            this.responsible = new String[]{"Tú"};
        } else {
            this.responsible = responsible;
        }
        if (dueDate.equals("")){
            this.dueDate = "-";
        } else{
            this.dueDate = dueDate;
        }

        if (category.equals("")){
            this.category = "Sin asignar";
        } else {
            this.category = category;
        }

        if (status.equals("Terminado")){
            this.completed = true;
        } else {
            this.completed = false;
        }

        // Añade los responsables y categorías a las listas correspondientes. Si están repetidas no
        // se guardan, ya que se usa un HashSet.

        // Constructor Pendientes()
        OrdenarPendientes.addCategoriesAvailable(this.category);
        OrdenarPendientes.addResponsiblesAvailable(this.responsible);

    }

    // Segundo método constructor. Este se usará solo de manera interna, al cargar los pendientes de
    // los archivos a la memoria ram.

    public Pendientes(String name, String description, String status, int priority,
                      @Nullable String[] responsible, String dueDate, String category,
                      String dateCreated, boolean completed, String lastEdit, int taskID){
        //Se aprovecha el otro constructor, para reusar código.
        this(name, description, status, priority, responsible, dueDate, category, dateCreated);

        // y se añaden los 3 atributos que faltaban.
        this.lastEdit = lastEdit;
        this.taskID = taskID;
        this.completed = completed;
    }

    // Métodos para el manejo de información del almacenamiento persistente hacia la ram, y de la
    // ram a la persistente.

    // Métodos "iniciadores". Del almacenamiento persistente a la memoria ram.
    // Se utiliza el sistema de ficheros de android. Se llaman en onCreate() de la clase
    // principal. En la clase principal hay un metodo auxiliar que manda los objetos bufferedReaders
    // para que aquí se procesen. (Esto se podría hacer aquí directamente, pero
    // eso implicaría que esta clase fuera subclase de "AppCompatActivity").

    // Llama a los dos métodos que inicializaran los HashMaps de pendientes.
    public static void initializeData(BufferedReader activeT, BufferedReader completedT){
        numberOfTasks = 0;
        processText(retrieveText(activeT), 0);
        processText(retrieveText(completedT), 1);

    }

    // Inicializa los objetos de tipo HashMap<Integer, Pendientes>. Carga la información a estas
    // estructuras de datos para que se puedan utilizar en el resto del programa.
    // El parámetro retrieveText tiene que tener toda la información de los pendientes. El formato
    // que tiene que tener la información se puede consultar en el método toString de Pendientes.

    private static void processText(String retrieveText, int ID) {

        // ID 0 = pendientes activos, no completados; ID 1: pendientes completados, inactivos.

        String[] lines = retrieveText.split("\n"); // Arreglo de todas las líneas del archivo.

        if (!(lines[0].split(SEPARATOR).length == 9)){
            // Validación de que el formato está correcto.
            return;
        }

        //System.out.println("Number of lines: " + lines.length);  // Usado para debugging
        //System.out.println("Number of Tasks: " + numberOfTasks);

        boolean completed; // Usado para el 9no argumento.

        if (ID == 0){
            completed = false;
        } else {
            completed = true;
        }

        // Se guarda temporalmente en este HashMap, con el id se determina a cual HashMap se le
        // asignará.

        HashMap<Integer, Pendientes> temporary = new HashMap<Integer, Pendientes>();

        for (int x = 0; x<lines.length;x++){
            String[] aValues = lines[x].split(SEPARATOR); // Separa los valores de los atributos.

            // Se ponen los objetos de tipo Pendientes en el HashMap. Se usa la información que está
            // separada por SEPARATOR. El 4to argumento se parsea a int porque asi es el constructor.
            // El 5to argumento, correspondiente a los responsables, es un arreglo de Strings. En el
            // archivo van a estar separados por "@@@". El id será el mismo que la
            // posición en la que estará guardado el objeto Pendientes en el HashMap, para borrar
            // los pendientes más facilmente.

            // Se usa un try catch para que no deje de funcionar el programa en caso de que algo
            // salga mal.
            try {
                temporary.put(numberOfTasks, new Pendientes(aValues[0], aValues[1], aValues[2],
                        Integer.parseInt(aValues[3]), aValues[4].split("@@@"),
                        aValues[5], aValues[6], aValues[7], completed, aValues[8], numberOfTasks));
            } catch (Exception e){
                // Para debugear.
                System.out.println("ERROR en el pendiente " + aValues[0]);
                e.printStackTrace();
            }

        }

        // Asigna el HashMap temporal al que corresponda.
        if (ID == 0){
            activeTasks = temporary;
        } else if(ID == 1){
            completedTasks = temporary;
        }

    }

    // Consigue el texto completo de los archivos de texto del programa.
    private static String retrieveText(BufferedReader br) {
        try{
            String line = br.readLine();
            String completeText = "";

            // Recorre linea por linea hasta que el texto completo está en completeText.
            while (line != null){
                completeText += line + "\n";
                line = br.readLine();
            }
            return completeText;
        } catch(IOException ignored){
        }
        return ""; // Si no hay texto en el BufferedReader no se regresa nada.
    }

    // Métodos para guardar la info. Pasarán la información de los hashmaps a una string. Esta
    // string se retornará para poder guardarse desde la clase principal.

    // Llamado en onStop() de la clase principal. Se utiliza el sistema de ficheros de andorid.
    public static String retrieveDataText(String fileName){

        String allText = "";
        Set entrySet;
        Iterator it;
        Map.Entry<Integer, Pendientes> entry;

        // Se elige el HashMap que se iterará, dependiendo del nombre que se ingrese al método.

        if (fileName.equals("activeTasks.txt")){
            entrySet = Pendientes.activeTasks.entrySet();
        } else if (fileName.equals("completedTasks.txt")){
            entrySet = Pendientes.completedTasks.entrySet();
        } else {
            return "Error, se ingresó un nombre de archivo incorrecto";
        }

        it = entrySet.iterator();

        // Se itera el archivo. Se pone el texto completo en una variable. Cada linea del archivo
        // tendrá la información de un objeto de tipo Pendientes.
        while(it.hasNext()){
            entry = (Map.Entry<Integer, Pendientes>) it.next();
            // El método toString regresa la información del pendiente, en el formato correcto.
            allText += entry.getValue().toString() + "\n"; // Añade los valores de cada objeto.
        }

        return allText; // Contiene la información completa
    }

    // Método toString(). Se utilizará para pasar la información de los objetos a texto. Generará
    // una string con los datos en el orden en el que se deben ingresar al constructor, separados
    // por comas.

    // El formato es el siguiente (de como se va a guardar en el archivo de texto):
    // name,description,status,priority,responsible,dueDate,category,dateCreated,lastEdit
    @Override
    public String toString(){

        // Antes se usaba concatenación directa, pero se cambió a StringBuilder para mejorar el
        // tiempo de ejecución y esto es importante porque se utiliza mucho este método toString.

        StringBuilder taskDataLine = new StringBuilder();

        taskDataLine.append(this.name).append(SEPARATOR);
        taskDataLine.append(this.description).append(SEPARATOR);
        taskDataLine.append(this.status).append(SEPARATOR);
        taskDataLine.append(this.priority).append(SEPARATOR);

        // Los responsables son separados por "@@@"
        for (int x = 0; x<responsible.length;x++){
            taskDataLine.append(responsible[x]).append("@@@");
        }

        // Borra los últimos "@@@".
        taskDataLine = new StringBuilder(taskDataLine.substring(0, taskDataLine.length() - 3));

        taskDataLine.append(SEPARATOR);
        taskDataLine.append(this.dueDate).append(SEPARATOR);
        taskDataLine.append(this.category).append(SEPARATOR);
        taskDataLine.append(this.dateCreated).append(SEPARATOR);
        taskDataLine.append(this.lastEdit);

        return taskDataLine.toString();

        /* Implementación de antes, sin StringBuilder.
        String taskDataLine = "";
        taskDataLine+= this.name + SEPARATOR +
                this.description + SEPARATOR +
                this.status + SEPARATOR +
                this.priority + SEPARATOR;
        // Los responsables son separados por "@@@"
        for (int x = 0; x<responsible.length;x++){
            taskDataLine += responsible[x] + "@@@";
        }
        taskDataLine = taskDataLine.substring(0,taskDataLine.length()-3); // Borra los últimos "@@@".
        if (responsible.length == 0){
            taskDataLine += "Tú";
        }
        taskDataLine += SEPARATOR;
        taskDataLine +=
                this.dueDate + SEPARATOR +
                this.category + SEPARATOR +
                this.dateCreated + SEPARATOR +
                this.lastEdit;

        return taskDataLine;
        */
    }

    // Usado para añadir tareas desde la actividad donde se registran.
    public static void addTask (Pendientes p){
        if (p.status.equals("Terminado")){
            p.completed = true; // Esto hace que se puedan agregar pendientes completados desde la
                                // clase para agregar pendientes.

            completedTasks.put(numberOfTasks-1, p); //Añade el pendiente, se le resta uno porque ese
                                                    // es el id del pendiente nuevo.
        } else {
            activeTasks.put(numberOfTasks-1, p); // Añade el pendiente a los activos.
        }

    }

    // Borra un pendiente para siempre.
    public static void deleteTask(Pendientes p){
        if (p.completed){
            completedTasks.remove(p.taskID);
        } else {
            activeTasks.remove(p.taskID);
        }
    }

    // Cambia el pendiente de un hashmap a otro, es como un toggle. Se llama en el método que se
    // ejecuta cuando se presiona un botón del recyclerView.
    public static void toggleCompleted(Pendientes p){
        if (p.completed){
            p.setCompleted(false);
            activeTasks.put(p.taskID, p);
            completedTasks.remove(p.taskID);
        } else {
            p.setCompleted(true);
            completedTasks.put(p.taskID, p);
            activeTasks.remove(p.taskID);
        }
    }

    // Getters y setters

    public String getName() {
        return name;
    }

    public void setName(String name, String time) {
        this.name = name;
        this.lastEdit = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description, String time) {
        this.description = description;
        this.lastEdit = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status, String time) {
        this.status = status;
        this.lastEdit = time;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }

    public boolean getCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority, String time) {
        this.priority = priority;
        this.lastEdit = time;
    }

    public String[] getResponsible() {
        return responsible;
    }

    public void setResponsible(String[] responsible, String time) {
        this.responsible = responsible;
        this.lastEdit = time;
    }

    // Se removió el setter de dateCreated porque no haría mucho sentido que se pueda cambiar.
    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    // Se removió el setter de lastEdit porque no haría mucho sentido que se pueda cambiar.
    public String getLastEdit() {
        return lastEdit;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate, String time) {
        this.dueDate = dueDate;
        this.lastEdit = time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category, String time) {
        this.category = category;
        this.lastEdit = time;
    }

    public int getTaskID() {
        return taskID;
    }

    public static int getNumberOfTasks() {
        return numberOfTasks;
    }

    public static String[] getOptionsPriority() {
        return optionsPriority;
    }

    public static String[] getStatusOptions() {
        return statusOptions;
    }

    public static HashMap<Integer, Pendientes> getActiveTasks() {
        return activeTasks;
    }

    public static HashMap<Integer, Pendientes> getCompletedTasks() {
        return completedTasks;
    }
}
