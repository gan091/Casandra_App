package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CenizaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CenizaFragment#} factory method to
 * create an instance of this fragment.
 */
public class CenizaFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    boolean excepcion = false;
    String id_house, tipCenizaAnterior,tipCenizaActual;
    float pMatTcho,pEstGenCub,pMatElemApoyo,pIncCub,resCeniza;
    int materialTecho, estadoGeneralCubierta, materialElementoApoyo, anguloInclinacionCubierta;
    long idRegistro = -1;
    AlertDialog d = null;

    ArrayAdapter<String> aaMatCob,aaMatElemAp,aaMorfCub,aaIncCub,aaEstGenCub,aaTipCeniza;
    BaseDeDatos bdP;
    Button btnGenerar;
    Context context;
    EditText etPM,etPIC,etPMEA,etPEGC,etResCeniza,etObservaciones;
    Spinner spMatCob,spMatElemAp,spMorfCub,spIncCub,spEstGenCub,spTipCeniza;
    String [] opcMatCob = new String[] {" ","Policarbonato","Teja Barro","Teja Eternit","Teja Traslucida","Teja Zinc"};
    String [] opcMatElemAp = new String[] {" ","Vigas Concreto","Vigas Madera","Vigas Metalicas"};
    String [] opcMorfCub = new String[] {" ","A dos Aguas","Cobertizo","Con Faldones"};
    String [] opcIncCub = new String[] {" ","Levemente Inclinada","Muy Inclinada"};
    String [] opcEstGenCub = new String[] {" ","Bueno","Regular","Malo"};
    String [] opcTipCeniza = new String[] {" ","Tipologia_1","Tipologia_2","Tipologia_3","Tipologia_4"};
    String tipoTecho,tipCeniza;
    Switch swTecho;
    TextView tvCubiertas,tvEstGenCub,tvMatElemApoyo,tvAngInclCub,tvMorfCub;

    public CenizaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(View view) {
        Toast toast;

        switch (view.getId()){
            case R.id.btnGenerarC:
                guardarBD();
                break;
            case R.id.tvMorfEst:
                toast= Toast.makeText(getActivity(),"Forma de Cubiertas segun el Escurrimiento",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvMaterial:
                toast= Toast.makeText(getActivity(),"Porcentaje segun Material (Resistencia de la Teja)",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvIncCub:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Inclinacion de la Cubierta",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvMEA:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Material del Elemento de Apoyo",Toast.LENGTH_SHORT);
                toast.show();
                break;
            case R.id.tvEstGenC:
                toast=Toast.makeText(getActivity(),"Porcentaje segun Estado General de la Cubierta",Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        bdP = new BaseDeDatos(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ceniza, container, false);

        btnGenerar = v.findViewById(R.id.btnGenerarC);

        spMatCob = v.findViewById(R.id.spMatCob);
        spMatElemAp = v.findViewById(R.id.spMatEleApoyo);
        spMorfCub = v.findViewById(R.id.spMorfEstCub);
        spIncCub = v.findViewById(R.id.spInclinacionCub);
        spEstGenCub = v.findViewById(R.id.spEstadoGeneralCub);
        spTipCeniza = v.findViewById(R.id.spTipCeniza);

        tvMorfCub = v.findViewById(R.id.tvMorfEst);
        tvCubiertas = v.findViewById(R.id.tvMaterial);
        tvEstGenCub = v.findViewById(R.id.tvIncCub);
        tvMatElemApoyo = v.findViewById(R.id.tvMEA);
        tvAngInclCub = v.findViewById(R.id.tvEstGenC);

        etPM = v.findViewById(R.id.etPM);
        etPIC = v.findViewById(R.id.etPIC);
        etPMEA = v.findViewById(R.id.etPMEA);
        etPEGC = v.findViewById(R.id.etPEGC);
        etResCeniza = v.findViewById(R.id.etResultadoCeniza);
        etObservaciones = v.findViewById(R.id.etObservacionesCeniza);

        aaTipCeniza = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_item,opcTipCeniza);
        aaMatCob = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_item,opcMatCob);
        aaMatElemAp = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_item,opcMatElemAp);
        aaIncCub = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_item,opcIncCub);
        aaEstGenCub = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_item,opcEstGenCub);
        aaMorfCub = new ArrayAdapter<>(this.getContext(),android.R.layout.simple_spinner_item,opcMorfCub);

        spTipCeniza.setAdapter(aaTipCeniza);
        spMatCob.setAdapter(aaMatCob);
        spMatElemAp.setAdapter(aaMatElemAp);
        spIncCub.setAdapter(aaIncCub);
        spEstGenCub.setAdapter(aaEstGenCub);
        spMorfCub.setAdapter(aaMorfCub);

        //spTipCeniza.setOnItemSelectedListener(this);

        tvMorfCub.setOnClickListener(this);
        tvCubiertas.setOnClickListener(this);
        tvEstGenCub.setOnClickListener(this);
        tvMatElemApoyo.setOnClickListener(this);
        tvAngInclCub.setOnClickListener(this);

        btnGenerar.setOnClickListener(this);

        Bundle bundle = getActivity().getIntent().getExtras();

        id_house = bundle.getString("idcasa");

        habilitarCubierta();

        swTecho = (Switch) v.findViewById(R.id.swTecho);

        swTecho.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (swTecho.isChecked()){
                    inhabilitarCubierta();
                }
                else{
                    habilitarCubierta();
                }
            }
        });

        cargarDatos();

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void cargarDatos(){

        bdP.abrirBD();
        Cursor cursorCeniza = bdP.cargarDatos_ID_RID(id_house,"tblceniza");

        /*
        0 -> RowId
        1 -> IdCasa
        2 -> Tipo de Techo
        3 -> Material de Cobertura
        4 -> Material Vigas de Apoyo
        5 -> Forma de Cubierta
        6 -> Inclinacion de Cubierta
        7 -> Estado General de Cubierta
        8 -> Tipologia Caida de Ceniza
        9 -> Observaciones
         */

        /*Log.i("Cas -> Tipo Techo","Antes de Condicion");
        Log.i("Cas -> Id Casa",Long.toString(idCasa));
        Log.i("Cas -> Cursor Vacio",Integer.toString(cursorCeniza.getCount()));
        cursorCeniza.moveToFirst();
        Log.i("Cas -> Tipo Techo",cursorCeniza.getString(2));*/
        if (cursorCeniza.moveToFirst()){
            idRegistro = cursorCeniza.getLong(0);

            if (cursorCeniza.getString(2).equals("Losas")){
                swTecho.setChecked(true);
                spTipCeniza.setSelection(4);
                etObservaciones.setText(cursorCeniza.getString(9));
                //idRegistro = cursorCeniza.getLong(0);
            }
            else{

            /*Tipos
            1 -> Material de Cobertura
            2 -> Material de Elemento de Apoyo
            3 -> Forma de Cubiertas
            4 -> Inclinacion de Cubierta
            5 -> Estado General de la Cubierta
            6 -> Tipologia de Caida de Ceniza*/

                do {
                    setSpinner(cursorCeniza.getString(3),1);
                    setSpinner(cursorCeniza.getString(4),2);
                    setSpinner(cursorCeniza.getString(5),3);
                    setSpinner(cursorCeniza.getString(6),4);
                    setSpinner(cursorCeniza.getString(7),5);
                    setSpinner(cursorCeniza.getString(8),6);
                    etObservaciones.setText(cursorCeniza.getString(9));
                    //idRegistro = cursorCeniza.getLong(0);
                }while (cursorCeniza.moveToNext());
            }
        }
        bdP.cerrarBD();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void guardarBD(){

        if (bdP.existeRegistro_RID(idRegistro,"tblceniza")){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

            builder1.setTitle("Registro Existente")
                    .setMessage("Ya hay registros almacenados previamente ¿Desea actualizar los registros?")
                    .setPositiveButton("Si",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    tipificacionCeniza();

                                    if (!excepcion){
                                        if (swTecho.isChecked() == false){
                                            try {
                                                bdP.abrirBD();
                                                bdP.updateCaidadeCeniza_RID(
                                                        idRegistro,
                                                        tipoTecho,
                                                        spMatCob.getSelectedItem().toString(),
                                                        spMatElemAp.getSelectedItem().toString(),
                                                        spMorfCub.getSelectedItem().toString(),
                                                        spIncCub.getSelectedItem().toString(),
                                                        spEstGenCub.getSelectedItem().toString(),
                                                        spTipCeniza.getSelectedItem().toString(),
                                                        etObservaciones.getText().toString().replaceAll("\n", ""));
                                                bdP.cerrarBD();

                                                Toast toast;
                                                toast=Toast.makeText(context,"Actualizacion de Registro Completada",Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                            catch (Exception e){
                                                Toast toast;
                                                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                        }
                                        else {
                                            try {
                                                bdP.abrirBD();
                                                bdP.updateCaidadeCeniza_RID(
                                                        idRegistro,
                                                        tipoTecho,
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        "",
                                                        spTipCeniza.getSelectedItem().toString(),
                                                        etObservaciones.getText().toString().replaceAll("\n", ""));
                                                bdP.cerrarBD();

                                                Toast toast;
                                                toast=Toast.makeText(context,"Actualizacion de Registro Completada",Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                            catch (Exception e){
                                                Toast toast;
                                                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                                toast.show();
                                            }
                                        }
                                    }
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    d.dismiss();
                                }
                            });
            d = builder1.create();
            d.show();
        }
        else {
            String matCob="",matEle="",forma="",inclinacion="",estado="",observaciones;
            if (!swTecho.isChecked()){
                if (spMatCob.getSelectedItem().toString().equals(" ") &&
                    spMatCob.getSelectedItem().toString().equals(" ") &&
                    spMatElemAp.getSelectedItem().toString().equals(" ") &&
                    spMorfCub.getSelectedItem().toString().equals(" ") &&
                    spIncCub.getSelectedItem().toString().equals(" ") &&
                    spEstGenCub.getSelectedItem().toString().equals(" ") &&
                    TextUtils.isEmpty(etObservaciones.getText().toString())
                ){
                    etObservaciones.requestFocus();
                    etObservaciones.setError("Al menos ingresar las observaciones de porque no se ha registrado ningun dato");
                }else {
                    tipificacionCeniza();
                    if (!excepcion){
                        matCob = spMatCob.getSelectedItem().toString();
                        matEle = spMatElemAp.getSelectedItem().toString();
                        forma = spMorfCub.getSelectedItem().toString();
                        inclinacion = spIncCub.getSelectedItem().toString();
                        estado = spEstGenCub.getSelectedItem().toString();
                        observaciones = etObservaciones.getText().toString();

                        try {
                            idRegistro = bdP.insertarCeniza_ID(
                                    id_house,
                                    tipoTecho,
                                    matCob,
                                    matEle,
                                    forma,
                                    inclinacion,
                                    estado,
                                    spTipCeniza.getSelectedItem().toString(),
                                    observaciones.replaceAll("\n", ""));

                            Toast toast;
                            toast=Toast.makeText(context,"Tipologia Caida de Ceniza - Guardado Exitoso",Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        catch (Exception e){
                            Toast toast;
                            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                }
            }else{
                tipificacionCeniza();
                observaciones = etObservaciones.getText().toString();

                try {
                    idRegistro = bdP.insertarCeniza_ID(
                            id_house,
                            tipoTecho,
                            matCob,
                            matEle,
                            forma,
                            inclinacion,
                            estado,
                            spTipCeniza.getSelectedItem().toString(),
                            observaciones.replaceAll("\n", ""));

                    Toast toast;
                    toast=Toast.makeText(context,"Tipologia Caida de Ceniza - Guardado Exitoso",Toast.LENGTH_SHORT);
                    toast.show();
                }
                catch (Exception e){
                    Toast toast;
                    toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    public void habilitarCubierta(){
        // Se utiliza para habilitar el diligenciamiento de las caracteristicas de cubierta
        spMatCob.setEnabled(true);
        spMatElemAp.setEnabled(true);
        spMorfCub.setEnabled(true);
        spIncCub.setEnabled(true);
        spEstGenCub.setEnabled(true);
    }

    public void inhabilitarCubierta(){
        // Se utiliza para inhabilitar el diligenciamiento de las caracteristicas de cubierta
        // cuando el tipo de cobertura es losas
        spMatCob.setEnabled(false);
        spMatElemAp.setEnabled(false);
        spMorfCub.setEnabled(false);
        spIncCub.setEnabled(false);
        spEstGenCub.setEnabled(false);
    }

    public void setSpinner(String opc, int tipo){

        //Este metodo se encarga se asignar en la interfaz los valores que se descargan de la base de datos

        switch (tipo){
            case 1:
                switch (opc){
                    case " ":
                        spMatCob.setSelection(0);
                        break;
                    case "Policarbonato":
                        spMatCob.setSelection(1);
                        break;
                    case "Teja Barro":
                        spMatCob.setSelection(2);
                        break;
                    case "Teja Eternit":
                        spMatCob.setSelection(3);
                        break;
                    case "Teja Traslucida":
                        spMatCob.setSelection(4);
                        break;
                    case "Teja Zinc":
                        spMatCob.setSelection(5);
                        break;
                }
                break;
            case 2:
                switch (opc){
                    case " ":
                        spMatElemAp.setSelection(0);
                        break;
                    case "Vigas Concreto":
                        spMatElemAp.setSelection(1);
                        break;
                    case "Vigas Madera":
                        spMatElemAp.setSelection(2);
                        break;
                    case "Vigas Metalicas":
                        spMatElemAp.setSelection(3);
                        break;
                }
                break;
            case 3:
                switch (opc){
                    case " ":
                        spMorfCub.setSelection(0);
                        break;
                    case "A dos Aguas":
                        spMorfCub.setSelection(1);
                        break;
                    case "Cobertizo":
                        spMorfCub.setSelection(2);
                        break;
                    case "Con Faldones":
                        spMorfCub.setSelection(3);
                        break;
                }
                break;
            case 4:
                switch (opc){
                    case " ":
                        spIncCub.setSelection(0);
                        break;
                    case "Levemente Inclinada":
                        spIncCub.setSelection(1);
                        break;
                    case "Muy Inclinada":
                        spIncCub.setSelection(2);
                        break;
                }
                break;
            case 5:
                switch (opc){
                    case " ":
                        spEstGenCub.setSelection(0);
                        break;
                    case "Bueno":
                        spEstGenCub.setSelection(1);
                        break;
                    case "Regular":
                        spEstGenCub.setSelection(2);
                        break;
                    case "Malo":
                        spEstGenCub.setSelection(3);
                        break;
                }
                break;
            case 6:
                switch (opc){
                    case "Tipologia_1":
                        spTipCeniza.setSelection(1);
                        break;
                    case "Tipologia_2":
                        spTipCeniza.setSelection(2);
                        break;
                    case "Tipologia_3":
                        spTipCeniza.setSelection(3);
                        break;
                    case "Tipologia_4":
                        spTipCeniza.setSelection(4);
                        break;
                }
                break;
        }
    }

    public void tipificacionCeniza(){
        // Se verifica el tipo de cubierta de la vivienda

        if (swTecho.isChecked()){
            // Si el tipo de techo es Losa, se define que la Tipologia es 4 y las resistente a la caida de ceniza
            spTipCeniza.setSelection(4);
            tipoTecho = "Losas";
        }
        else{
            // Si el tipo es cubierta, se habilitan el resto de opciones del formulario y se evaluan las caracteristicas
            // de acuerdo a los criterios definidos al inicio del proyecto
            tipoTecho = "Cubierta";
            if(
                    spMatCob.getSelectedItem().toString().equals("Teja Barro") &&
                    spIncCub.getSelectedItem().toString().equals("Levemente Inclinada")
            ){
                advertencia("Inconsistencia en los Registros",
                        "Para el uso de Tejas de Barro, se debe contar con una buena inclinacion de cubierta. \n\n" +
                                "Verificar el tipo de material de cubierta y/o la inclinacion de las misma");
            }
            else{
                if (
                        spMatCob.getSelectedItem().toString().equals("Teja Barro") &&
                        spIncCub.getSelectedItem().toString().equals("Muy Inclinada") &&
                        spMatElemAp.getSelectedItem().toString().equals("Vigas Metalicas") &&
                        spEstGenCub.getSelectedItem().toString().equals("Malo")
                ){
                    advertencia("Inconsistencia en los Registros",
                            "Para el uso de Tejas de Barro, no es muy recomendable el uso de Vigas Metalicas como elemento de apoyo" +
                                    " de cubierta, ya que presenta poca cohesion entre ambas. \n\n" +
                                    "Verificar el tipo de material de cubierta y/o elemento de apoyo de las misma");
                }
                else {
                    excepcion = false;
                    String matCob = spMatCob.getSelectedItem().toString();
                    // Se le asigna un puntaje especifico de acuerdo al material de cobertura
                    // Si es Teja Traslucida, Teja de Zinc o Teja de Policarbonato se asigna 10
                    // Si es Teja de Eternit se asigna 5
                    // Si es Teja de Barro se asigna 1
                    if(matCob.equals("Teja Traslucida") || matCob.equals("Teja Zinc") || matCob.equals("Policarbonato")){
                        materialTecho = 10;
                    }
                    else{
                        if(matCob.equals("Teja Eternit")){
                            materialTecho = 5;
                        }else
                        {
                            materialTecho = 1;
                        }
                    }

                    String incCub = spIncCub.getSelectedItem().toString();
                    // Se le asigna un puntaje especifico de acuerdo a la inclinacion de la cubierta
                    // Si es Levemente Inclinada o no se define inclinacion se asigna 10
                    // Si es Muy Inclinada se asigna 5
                    if(incCub.equals("Levemente Inclinada") || incCub.equals(" ")){
                        anguloInclinacionCubierta = 10;
                    }
                    else {
                        anguloInclinacionCubierta = 5;
                    }

                    String matElemAp = spMatElemAp.getSelectedItem().toString();
                    // Se le asigna un puntaje especifico de acuerdo al material del elemento de apoyo de la cubierta
                    // Si son Vigas de Madera o no se define material de elemento de apoyo se asigna 10
                    // Si son Vigas Metalicas se asigna 3
                    // Si son Vigas en Concreto se asigna 1
                    if(matElemAp.equals("Vigas Madera") || matElemAp.equals(" ")){
                        materialElementoApoyo = 10;
                    }
                    else{
                        if(matElemAp.equals("Vigas Metalicas")){
                            materialElementoApoyo = 3;
                        }
                        else{
                            if(matElemAp.equals("Vigas Concreto")){
                                materialElementoApoyo = 1;
                            }
                        }
                    }

                    String estGC = spEstGenCub.getSelectedItem().toString();
                    // Se le asigna un puntaje especifico de acuerdo al estado general de la cubierta
                    // Si es Malo o no se define el estado general de la cubierta se asigna 10
                    // Si es Regular se asigna 5
                    // Si es Bueno se asigna 1
                    if(estGC.equals("Malo") || estGC.equals(" ")){
                        estadoGeneralCubierta = 10;
                    }
                    else{
                        if(estGC.equals("Regular")){
                            estadoGeneralCubierta = 5;
                        }
                        else{
                            estadoGeneralCubierta = 1;
                        }
                    }

                    // Una vez asignado los puntajes por caracteristica de cubierta se define el porcentaje de influencia
                    // en la tipificacion estructural frente a la amenaza volcanica Caida de Ceniza y se multiplica para obtener
                    // el resultado que definira la tipificacion
                    // Para material de cubierta un 40%
                    // Para angulo de inclinacion un 15%
                    // Para material de elemento de apoyo un 35%
                    // Para estado general de cubierta un 10%
                    pMatTcho = (float) (materialTecho *0.4);
                    pIncCub = (float) (anguloInclinacionCubierta *0.15);
                    pMatElemApoyo = (float) (materialElementoApoyo *0.35);
                    pEstGenCub = (float) (estadoGeneralCubierta *0.10);

                    // Al sumar los resultados obtenidos de las caracteristicas de la cubierta
                    // y despues de aplicar sus respectivos porcentajes, se obtiene el resultado que definira el tipo de Tipologia
                    // Estructural frente a la amenaza volcanica Caida de Ceniza que aplica para la vivienda
                    resCeniza = bdP.reducirFloat(pMatTcho + pEstGenCub + pMatElemApoyo + pIncCub);


                    // Se muestra en la interfaz los resultados obtenidos al realizar los calculos para determinar la tipologia
                    etPM.setText(Float.toString(bdP.reducirFloat(pMatTcho)));
                    etPIC.setText(Float.toString(bdP.reducirFloat(pIncCub)));
                    etPMEA.setText(Float.toString(bdP.reducirFloat(pMatElemApoyo)));
                    etPEGC.setText(Float.toString(bdP.reducirFloat(pEstGenCub)));
                    etResCeniza.setText(Float.toString(bdP.reducirFloat(resCeniza)));

                    // Tipologias
                    // 0 -> Sin Tipologia
                    // 1-2-3 -> Tipologia 1,2,3 => Si el resultado concuerda con el valor de alguna de las
                    // combinaciones se asigna la tipificacion adecuada

                    if(
                            resCeniza == 4.1f
                         || resCeniza == 4.8f
                         || resCeniza == 4.85f
                         || resCeniza == 5.05f
                         || resCeniza == 5.55f
                         || resCeniza == 5.65f
                         || resCeniza == 6.1f
                         || (resCeniza >= 6.3f && resCeniza <= 10)
                    )
                    {
                        spTipCeniza.setSelection(1);
                    }
                    else{
                        if(        resCeniza == 2.5f
                                || resCeniza == 2.6f
                                || (resCeniza >= 3.95f && resCeniza <= 4.74f)
                                || resCeniza == 5.15f
                                || resCeniza == 5.2f
                                || resCeniza == 5.6f
                                || resCeniza == 5.9f){
                            spTipCeniza.setSelection(2);
                        }
                        else{
                            if (       resCeniza == 5.95f
                                    || resCeniza <= 2.4f
                                    || (resCeniza >= 2.7f && resCeniza <= 3.2f)
                                    || (resCeniza >= 3.6f && resCeniza <= 3.9f)
                                    || resCeniza == 4.75f){
                                spTipCeniza.setSelection(3);
                            }
                        }
                    }
                }

            }
        }
    }

    public void advertencia(String titulo, String mensaje){
        excepcion = true;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());

        builder1.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                d.dismiss();
                            }
                        })
                .setIcon(R.drawable.common_google_signin_btn_icon_disabled);
        d = builder1.create();
        d.show();

        // Must call show() prior to fetching views
        TextView messageView = d.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
    }
}
