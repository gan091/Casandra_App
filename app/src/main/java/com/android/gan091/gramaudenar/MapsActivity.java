package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.gan091.gramaudenar.usermanagement.GestionUsuario;
import com.android.gan091.gramaudenar.usermanagement.MostrarUsuario;
import com.android.gan091.gramaudenar.usermanagement.RegistrarUsuarios;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.nio.channels.FileChannel;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    boolean contains = false;
    char sector = 'a';
    String latitud,longitud;
    int idCasa,corregimiento =10;
    private GoogleMap mMap;
    GestionUsuario gesUs;

    AlertDialog d = null;
    NavigationView navigationView;
    ArrayAdapter<String> aaSector,aaCorregimiento;
    BaseDeDatos bdP;
    Button btnActualizar;
    Context context;
    LatLng camara;

    PolygonOptions pOpFSA;
    PolygonOptions pOpRSA, pOpRSB, pOpRSC, pOpRSD,pOpRSE,pOpRSF,pOpRSG;
    PolygonOptions pOpRoSA, pOpRoSB, pOpRoSC, pOpRoSD,pOpRoSE,pOpRoSF,pOpRoSG,pOpRoSH,pOpRoSI;
    PolygonOptions pOpTSA, pOpTSB, pOpTSC, pOpTSD,pOpTSE;
    PolygonOptions pOpMSA, pOpMSB, pOpMSC, pOpMSD,pOpMSE,pOpMSF,pOpMSG,pOpMSH;

    Spinner spSector,spCorregimiento;

    String [] opcCorregimiento = new String[] {"Rural","La Florida","Matituy","Tunja","Robles","Rodeo"};
    String [] opcSecLaF = new String[] {"Sector FA","Sector FB","Sector FC"};
    String [] opcSecR = new String[] {"Sector RA","Sector RB","Sector RC","Sector RD","Sector RE","Sector RF","Sector RG"};
    String [] opcSecRo = new String[] {"Sector ROA","Sector ROB","Sector ROC","Sector ROD","Sector ROE","Sector ROF","Sector ROG","Sector ROH","Sector ROI"};
    String [] opcSecT = new String[] {"Sector TA","Sector TB","Sector TC","Sector TD","Sector TE"};
    String [] opcSecM = new String[] {"Sector MA","Sector MB","Sector MC","Sector MD","Sector ME","Sector MF","Sector MG","Sector MH"};
    String [] opcSecRu = new String[] {""};
    String [] archivoCad;

    boolean validarPermisos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbarAM);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        context = this;

        spSector = findViewById(R.id.spSector);
        spCorregimiento = findViewById(R.id.spCorregimiento);

        aaCorregimiento = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,opcCorregimiento);
        spCorregimiento.setAdapter(aaCorregimiento);

        btnActualizar = findViewById(R.id.btnActualizar);

        spSector.setVisibility(View.INVISIBLE);
        spCorregimiento.setVisibility(View.INVISIBLE);
        btnActualizar.setVisibility(View.INVISIBLE);

        bdP = new BaseDeDatos(context);

        bdP.abrirBD();
        Cursor cursorT = bdP.cargarDatos("tbltutorial");
        cursorT.moveToFirst();
        if (cursorT.getInt(0)==0){
            bdP.updateTutorial(1);
            Intent i = new Intent(MapsActivity.this, TutorialActivity.class);
            startActivity(i);
        }
        bdP.close();

        asignarCorregimiento("Rural");

        cargarZona();
        //Verificar que hace este condicional
        if(spCorregimiento.getSelectedItem().toString().equals("Rural")){
        }
        spSector.setOnItemSelectedListener(this);
        gesUs = new GestionUsuario(context);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        extras();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnActualizar:
                asignarCorregimiento(spCorregimiento.getSelectedItem().toString());
                break;
        }
    }

    public void agregarMarcador(){

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

                switch (corregimiento){
                    case 0:
                        contains = true;
                        break;
                    case 10:
                        switch (sector){
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng,pOpFSA.getPoints(),false);
                                break;
                        }
                        break;
                    case 20:
                        switch (sector){
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng,pOpRSA.getPoints(),false);
                                break;
                            case 'b':
                                contains = PolyUtil.containsLocation(latLng,pOpRSB.getPoints(),false);
                                break;
                            case 'c':
                                contains = PolyUtil.containsLocation(latLng,pOpRSC.getPoints(),false);
                                break;
                            case 'd':
                                contains = PolyUtil.containsLocation(latLng,pOpRSD.getPoints(),false);
                                break;
                            case 'e':
                                contains = PolyUtil.containsLocation(latLng,pOpRSE.getPoints(),false);
                                break;
                            case 'f':
                                contains = PolyUtil.containsLocation(latLng,pOpRSF.getPoints(),false);
                                break;
                            case 'g':
                                contains = PolyUtil.containsLocation(latLng,pOpRSG.getPoints(),false);
                                break;
                        }
                        break;
                    case 30:
                        switch (sector) {
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSA.getPoints(), false);
                                break;
                            case 'b':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSB.getPoints(), false);
                                break;
                            case 'c':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSC.getPoints(), false);
                                break;
                            case 'd':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSD.getPoints(), false);
                                break;
                            case 'e':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSE.getPoints(), false);
                                break;
                            case 'f':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSF.getPoints(), false);
                                break;
                            case 'g':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSG.getPoints(), false);
                                break;
                            case 'h':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSH.getPoints(), false);
                                break;
                            case 'i':
                                contains = PolyUtil.containsLocation(latLng, pOpRoSI.getPoints(), false);
                                break;
                        }
                        break;
                    case 40:
                        switch (sector) {
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng, pOpTSA.getPoints(), false);
                                break;
                            case 'b':
                                contains = PolyUtil.containsLocation(latLng, pOpTSB.getPoints(), false);
                                break;
                            case 'c':
                                contains = PolyUtil.containsLocation(latLng, pOpTSC.getPoints(), false);
                                break;
                            case 'd':
                                contains = PolyUtil.containsLocation(latLng, pOpTSD.getPoints(), false);
                                break;
                            case 'e':
                                contains = PolyUtil.containsLocation(latLng, pOpTSE.getPoints(), false);
                                break;
                        }
                        break;
                    case 50:
                        switch (sector) {
                            case 'a':
                                contains = PolyUtil.containsLocation(latLng, pOpMSA.getPoints(), false);
                                break;
                            case 'b':
                                contains = PolyUtil.containsLocation(latLng, pOpMSB.getPoints(), false);
                                break;
                            case 'c':
                                contains = PolyUtil.containsLocation(latLng, pOpMSC.getPoints(), false);
                                break;
                            case 'd':
                                contains = PolyUtil.containsLocation(latLng, pOpMSD.getPoints(), false);
                                break;
                            case 'e':
                                contains = PolyUtil.containsLocation(latLng, pOpMSE.getPoints(), false);
                                break;
                            case 'f':
                                contains = PolyUtil.containsLocation(latLng, pOpMSF.getPoints(), false);
                                break;
                            case 'g':
                                contains = PolyUtil.containsLocation(latLng, pOpMSG.getPoints(), false);
                                break;
                            case 'h':
                                contains = PolyUtil.containsLocation(latLng, pOpMSH.getPoints(), false);
                                break;
                        }
                        break;
                }

                if(contains){
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                }
            }
        });
    }

    public void asignarCorregimiento(String opcCorr){
        switch (opcCorr){
            case "Rural":
                corregimiento = 0;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecRu);
                break;
            case "La Florida":
                corregimiento = 10;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecLaF);
                break;
            case "Robles":
                corregimiento = 20;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecR);
                break;
            case "Rodeo":
                corregimiento = 30;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecRo);
                break;
            case "Tunja":
                corregimiento = 40;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecT);
                break;
            case "Matituy":
                corregimiento = 50;
                aaSector = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,opcSecM);
                break;
        }
        spSector.setAdapter(aaSector);
    }

    public void cargarDatos(){
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblcasa");
        Cursor cOCh, cC, cL;

        try {
            if (cursor.moveToFirst()){
                do {
                    idCasa = cursor.getInt(0);
                    latitud = cursor.getString(1);
                    longitud = cursor.getString(2);

                    int res = 0;

                    cOCh = bdP.cargarDatosTablas(idCasa,"tblondaChoque");

                    if(cOCh.moveToFirst()){
                        res = res + 1;
                    }

                    cC = bdP.cargarDatosTablas(idCasa,"tblceniza");
                    if(cC.moveToFirst()){
                        res = res + 1;
                    }

                    cL = bdP.cargarDatosTablas(idCasa,"tbllahares");
                    if(cL.moveToFirst()){
                        res = res + 1;
                    }

                        if (res == 3){
                            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_houseok);
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        }else {
                            if (res > 0 && res < 3){

                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                        .icon(BitmapDescriptorFactory.defaultMarker(25.0f)));
                            }
                            else {
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                            }
                        }

                        /*Cursor cursorTipologia = bdP.cargarDatosTablas(latitud,longitud,"tblCeniza");
                    if (cursorTipologia.moveToFirst()){
                        do{
                            String tip = cursorTipologia.getString(8);
                            if (tip.equals("Tipologia_3")){
                                mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                            }
                            else {
                                if (tip.equals("Tipologia_2")){
                                    mMap.addMarker(new MarkerOptions()
                                            .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                }
                                else{
                                        if (tip.equals("Tipologia_1")){
                                            Log.i("Coordenadas","Latitud: "+latitud+" Longitud: "+longitud);
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                        }
                                        else {
                                            mMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                        }
                                }
                            }
                        }while (cursorTipologia.moveToNext());
                    }*/
                    /*if (cursor.getInt(2) == 1){
                        mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    }
                    else {
                        if (cursor.getInt(2) == 2){
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        }
                        else {
                            mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud))));
                        }
                    }*/
                }while (cursor.moveToNext());
            }
        }
        catch (Exception e){
            Log.e("Base de Datos","Error al leer la base de datos");
        }
        bdP.close();
    }

    public void generarInfExtra(){
        bdP.abrirBD();

        Cursor cursor = bdP.cargarDatos("tblcasa");
        Cursor cursorF1 = bdP.cargarDatos("tblfachada1");
        Cursor cursorV1 = bdP.cargarDatos("tblventana1");
        Cursor cursorP1 = bdP.cargarDatos("tblpuerta1");

        try {
            if (cursorF1.getCount() != 0){
                bdP.limpiarTabla("tblfachada1");
            }

            if (cursorV1.getCount() != 0){
                bdP.limpiarTabla("tblventana1");
            }

            if (cursorP1.getCount() != 0){
                bdP.limpiarTabla("tblpuerta1");
            }

            if (cursor.moveToFirst()){
                do {
                    idCasa = cursor.getInt(0);

                    Cursor cF,cV,cP;

                    cF = bdP.cargarDatosTablas(idCasa,"tblfachada");

                    float ancM=0,altM=0,arM=0,arT=0;

                    if (cF.moveToFirst()){
                        do {
                            if(ancM == 0 && altM == 0){
                                ancM = cF.getFloat(2);
                                altM = cF.getFloat(3);
                                arM = cF.getFloat(4);
                            }else {
                                if(arM < cF.getFloat(4)){
                                    ancM = cF.getFloat(2);
                                    altM = cF.getFloat(3);
                                    arM = cF.getFloat(4);
                                }
                            }
                            arT+=cF.getFloat(4);
                        }while (cF.moveToNext());
                        bdP.insertarFachada1(idCasa,ancM,altM,arM,arT);
                    }else {
                        bdP.insertarFachada1(idCasa,0,0,0,0);
                    }

                    cV = bdP.cargarDatosTablas(idCasa,"tblventana");

                    float ancMV=0,altMV=0,arMV=0,ancMen=0,altMen=0,arMen=0,arTV=0,pAb=0;
                    int nPG=0,nPP=0;

                    if (cV.moveToFirst()){
                        do {
                            if (cV.getCount() == 1){
                                ancMV = cV.getFloat(2);
                                altMV = cV.getFloat(3);
                                arMV = cV.getFloat(4);
                                nPG = cV.getInt(5);
                            }else {
                                if (ancMV == 0 && altMV == 0 && ancMen == 0 && altMen == 0){
                                    ancMV = cV.getFloat(2);
                                    altMV = cV.getFloat(3);
                                    arMV = cV.getFloat(4);
                                    nPG = cV.getInt(5);
                                    ancMen = cV.getFloat(2);
                                    altMen = cV.getFloat(3);
                                    arMen = cV.getFloat(4);
                                    nPP = cV.getInt(5);
                                }else{
                                    if (arMV < cV.getFloat(4)){
                                        ancMV = cV.getFloat(2);
                                        altMV = cV.getFloat(3);
                                        arMV = cV.getFloat(4);
                                        nPG = cV.getInt(5);
                                    }
                                    if (arMen > cV.getFloat(4)){
                                        ancMen = cV.getFloat(2);
                                        altMen = cV.getFloat(3);
                                        arMen = cV.getFloat(4);
                                        nPP = cV.getInt(5);
                                    }
                                }
                            }
                            arTV+=cV.getFloat(4);
                        }while (cV.moveToNext());
                        /*if (arT == 0){

                        }else {
                            pAb = (arTV/arT)*100;
                        }*/
                        bdP.insertarVentana1(idCasa,ancMV,altMV,arMV,nPG,ancMen,altMen,arMen,nPP,arTV,pAb);
                    }else {
                        bdP.insertarVentana1(idCasa,0,0,0,0,0,0,0,0,0,0);
                    }
                    cP = bdP.cargarDatosTablas(idCasa,"tblpuerta");

                    ancM = 0;
                    altM = 0;
                    arM = 0;
                    arT = 0;

                    if (cP.moveToFirst()){
                        do {
                            if(ancM == 0 && altM == 0){
                                ancM = cP.getFloat(2);
                                altM = cP.getFloat(3);
                                arM = cP.getFloat(4);
                            }else {
                                if(arM < cP.getFloat(4)){
                                    ancM = cP.getFloat(2);
                                    altM = cP.getFloat(3);
                                    arM = cP.getFloat(4);
                                }
                            }
                            arT+=cP.getFloat(4);
                        }while (cP.moveToNext());
                        bdP.insertarPuerta1(idCasa,ancM,altM,arM,arT);
                    }else {
                        bdP.insertarPuerta1(idCasa,0,0,0,0);
                    }

                }while (cursor.moveToNext());
            }

        }catch (Exception e){
            Toast toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        bdP.cerrarBD();
    }

    public void extras(){
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                final Marker m = marker;
                latitud = Double.toString(setLatitud(m));
                longitud = Double.toString(setLongitud(m));
                idCasa = bdP.getIdCasa(latitud,longitud);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                final CharSequence[] items = new CharSequence[4];

                items[0] = "Eliminar Marcador";
                items[1] = "Agregar Casa";
                items[2] = "Eliminar Casa";
                items[3] = "Fijar Camara";

                builder.setTitle("Opciones")
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        m.remove();
                                        break;
                                    case 1:
                                        if(bdP.existeRegistro("tblcasa",idCasa)){
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

                                            builder1.setTitle("Registro existente")
                                                    .setMessage("Ya hay una vivienda registrada con estas coordenadas ¿Desea modificar su registro?")
                                                    .setPositiveButton("Si",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    instanciarCasa(idCasa);
                                                                }
                                                            })
                                                    .setNegativeButton("No",
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {}
                                                            });
                                            builder1.create().show();
                                        }
                                        else {
                                            Toast toast;
                                            try {
                                                bdP.abrirBD();
                                                long insert = bdP.insertarCasa(latitud,longitud,zonas());
                                                bdP.cerrarBD();
                                                idCasa = bdP.getIdCasa(latitud,longitud);
                                                instanciarCasa(idCasa);
                                            } catch (Exception e) {
                                                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                                toast.show();
                                            }

                                        }
                                        break;
                                    case 2:
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);

                                        builder1.setTitle("Eliminar Casa")
                                                .setMessage("¿Esta seguro que desea eliminar los registros de la casa seleccionada?")
                                                .setPositiveButton("Si",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                try {
                                                                    if (bdP.existeRegistro("tblcasa",idCasa)){
                                                                        bdP.eliminarRegistro("tblcasa",idCasa);
                                                                        if (bdP.existeRegistro("tblondachoque",idCasa)){
                                                                            bdP.eliminarRegistro("tblondachoque",idCasa);
                                                                        }
                                                                        if (bdP.existeRegistro("tblfachada",idCasa)){
                                                                            bdP.eliminarRegistro("tblfachada",idCasa);
                                                                        }
                                                                        if (bdP.existeRegistro("tblfachada1",idCasa)){
                                                                            bdP.eliminarRegistro("tblfachada1",idCasa);
                                                                        }
                                                                        if (bdP.existeRegistro("tblventana",idCasa)){
                                                                            bdP.eliminarRegistro("tblventana",idCasa);
                                                                        }
                                                                        if (bdP.existeRegistro("tblventana1",idCasa)){
                                                                            bdP.eliminarRegistro("tblventana1",idCasa);
                                                                        }
                                                                        if (bdP.existeRegistro("tblpuerta",idCasa)){
                                                                            bdP.eliminarRegistro("tblpuerta",idCasa);
                                                                        }
                                                                        if (bdP.existeRegistro("tblpuerta1",idCasa)){
                                                                            bdP.eliminarRegistro("tblpuerta1",idCasa);
                                                                        }
                                                                        if (bdP.existeRegistro("tbllahares",idCasa)){
                                                                            bdP.eliminarRegistro("tbllahares",idCasa);
                                                                        }
                                                                        if (bdP.existeRegistro("tblCeniza",idCasa)){
                                                                            bdP.eliminarRegistro("tblCeniza",idCasa);
                                                                        }
                                                                    }
                                                                    m.remove();
                                                                } catch (Exception e) {
                                                                    Log.i("EliminarCasa",e.toString());
                                                                    Toast toast;
                                                                    toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                                                    toast.show();
                                                                }
                                                            }
                                                        })
                                                .setNegativeButton("No",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {}
                                                        });
                                        builder1.create().show();
                                        break;
                                    case 3:
                                        try {
                                            bdP.insertarCamara(latitud,longitud);
                                        }
                                        catch (Exception e){
                                            Toast toast;
                                            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                        break;
                                }
                            }
                        });

                builder.create();
                builder.show();

                return false;
            }
        });
    }

    public void instanciarCasa(int idCasa){
        Intent i = new Intent(MapsActivity.this, FormulariosActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("idcasa",idCasa);

        i.putExtras(bundle);
        startActivity(i);
        overridePendingTransition(R.anim.left_in,R.anim.left_out);
    }

    public double setLatitud(Marker marker){
        return marker.getPosition().latitude;
    }

    public double setLongitud(Marker marker){
        return marker.getPosition().longitude;
    }

    public String zonas(){
        String zona="";
        switch(corregimiento){
            case 0:
                zona = "Rural";
                break;
            case 10:
                zona = "La Florida";
                break;
            case 20:
                zona = "Robles";
                break;
            case 30:
                zona = "Rodeo";
                break;
            case 40:
                zona = "Tunja";
                break;
            case 50:
                zona = "Matituy";
                break;
            default:
                zona = "Error";
                break;
        }
        return zona;
    }

    public void fijarZona(){
        try {
            bdP.insertar(spCorregimiento.getSelectedItem().toString(),spSector.getSelectedItemPosition());
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        //super.onBackPressed();
    }

    public void cargarZona(){
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("corregimiento","sector","tblzona");
        cursor.moveToFirst();
        String c;
        int s;
        do {
            c = cursor.getString(0);
            s = cursor.getInt(1);
        }while (cursor.moveToNext());
        bdP.cerrarBD();

        asignarCorregimiento(c);
        spSector.setSelection(s);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mMap.clear();
        cargarDatos();

        switch (spSector.getSelectedItem().toString()){
            case "":
                Cursor cursor = bdP.cargarDatos("latitud","longitud","tblcamara");

                if (cursor.moveToFirst()){
                    do {
                        camara = new LatLng(cursor.getDouble(0),cursor.getDouble(1));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,15));
                    }while (cursor.moveToNext());
                }
                break;
            case "Sector FA":
                pOpFSA = new PolygonOptions()
                        .add(new LatLng(1.297758, -77.404716),
                                new LatLng(1.299492, -77.404890),
                                new LatLng(1.299807, -77.404562),
                                new LatLng(1.299816, -77.402587),
                                new LatLng(1.298172, -77.402851),
                                new LatLng(1.297664, -77.404094),
                                new LatLng(1.297855, -77.404143))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);
                camara = new LatLng(1.299816, -77.402587);
                mMap.addPolygon(pOpFSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,16));
                break;
            case "Sector RA":
                pOpRSA = new PolygonOptions()
                        .add(new LatLng(1.3410625,-77.4189109),
                                new LatLng(1.3408158,-77.4193829),
                                new LatLng(1.3404136,-77.4196887),
                                new LatLng(1.3399792,-77.4199462),
                                new LatLng(1.3376892,-77.418809),
                                new LatLng(1.338086,-77.4179399),
                                new LatLng(1.3403921,-77.4183798))
                        .strokeColor(Color.BLUE)
                        .strokeWidth(3);
                camara = new LatLng(1.3394682268,-77.4189103679);
                mMap.addPolygon(pOpRSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector RB":
                pOpRSB = new PolygonOptions()
                        .add(new LatLng(1.3408694,-77.4193937),
                                new LatLng(1.342666,-77.4196887),
                                new LatLng(1.3424891,-77.420429),
                                new LatLng(1.342001,-77.4203324),
                                new LatLng(1.3408212,-77.4205363),
                                new LatLng(1.3401562,-77.4198872))
                        .strokeColor(Color.CYAN)
                        .strokeWidth(3);
                camara = new LatLng(1.3415374697,-77.4199251444);
                mMap.addPolygon(pOpRSB);
                sector = 'b';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector RC":
                pOpRSC = new PolygonOptions()
                        .add(new LatLng(1.3425802,-77.4201232),
                                new LatLng(1.3427787,-77.4197853),
                                new LatLng(1.3451008,-77.4190342),
                                new LatLng(1.3463236,-77.4186587),
                                new LatLng(1.3477662,-77.4197048),
                                new LatLng(1.3475678,-77.4207079),
                                new LatLng(1.3468438,-77.4206436),
                                new LatLng(1.3466024,-77.4205953),
                                new LatLng(1.346404,-77.4197102),
                                new LatLng(1.3427572,-77.4205363),
                                new LatLng(1.3425588,-77.4202949))
                        .strokeColor(Color.MAGENTA)
                        .strokeWidth(3);
                camara = new LatLng(1.3452881125,-77.4195969187);
                mMap.addPolygon(pOpRSC);
                sector = 'c';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector RD":
                pOpRSD = new PolygonOptions()
                        .add(new LatLng(1.3475249,-77.4207187),
                                new LatLng(1.3479888,-77.4209145),
                                new LatLng(1.3478439,-77.4213946),
                                new LatLng(1.3476669,-77.4218425),
                                new LatLng(1.3470905,-77.4217004),
                                new LatLng(1.3467419,-77.420665))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);
                camara = new LatLng(1.3474422261,-77.4211804527);
                mMap.addPolygon(pOpRSD);
                sector = 'd';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector RE":
                pOpRSE = new PolygonOptions()
                        .add(new LatLng(1.3487932,-77.4217781),
                                new LatLng(1.3485277,-77.4218827),
                                new LatLng(1.3476991,-77.4217808),
                                new LatLng(1.3479137,-77.4211586),
                                new LatLng(1.3486108,-77.4210164))
                        .strokeColor(Color.WHITE)
                        .strokeWidth(3);
                camara = new LatLng(1.3483590297,-77.4214654691);
                mMap.addPolygon(pOpRSE);
                sector = 'e';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,19));
                break;
            case "Sector RF":
                pOpRSF = new PolygonOptions()
                        .add(new LatLng(1.3487932,-77.4217057),
                                new LatLng(1.3486162,-77.4210003),
                                new LatLng(1.3494287,-77.4206704),
                                new LatLng(1.3523729,-77.4201393),
                                new LatLng(1.352558,-77.4208179),
                                new LatLng(1.3522335,-77.422092))
                        .strokeColor(Color.YELLOW)
                        .strokeWidth(3);
                camara = new LatLng(1.3507437258,-77.4210808658);
                mMap.addPolygon(pOpRSF);
                sector = 'f';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector RG":
                pOpRSG = new PolygonOptions()
                        .add(new LatLng(1.3493536,-77.422049),
                                new LatLng(1.3480772,-77.4230736),
                                new LatLng(1.347187,-77.4225801),
                                new LatLng(1.3475141,-77.4218184),
                                new LatLng(1.3481523,-77.4218988),
                                new LatLng(1.3485224,-77.4219364),
                                new LatLng(1.3489192,-77.4217594))
                        .strokeColor(Color.LTGRAY)
                        .strokeWidth(3);
                camara = new LatLng(1.3482122129,-77.4222849837);
                mMap.addPolygon(pOpRSG);
                sector = 'g';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector ROA":
                pOpRoSA = new PolygonOptions()
                        .add(new LatLng(1.3126441,-77.4355406),
                                new LatLng(1.3119093,-77.4360341),
                                new LatLng(1.311121,-77.4353957),
                                new LatLng(1.3115447,-77.4346823),
                                new LatLng(1.3123062,-77.434237),
                                new LatLng(1.313234,-77.4347252))
                        .strokeColor(Color.BLUE)
                        .strokeWidth(3);

                camara = new LatLng(1.3122145292,-77.4349648110);
                mMap.addPolygon(pOpRoSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector ROB":
                pOpRoSB = new PolygonOptions()
                        .add(new LatLng(1.3123062,-77.434237),
                                new LatLng(1.3115447,-77.4346823),
                                new LatLng(1.3102736,-77.4336416),
                                new LatLng(1.3111371,-77.4324614),
                                new LatLng(1.3123759,-77.4322093),
                                new LatLng(1.3131053,-77.4329603))
                        .strokeColor(Color.CYAN)
                        .strokeWidth(3);

                camara = new LatLng(1.3117131545,-77.4332049749);
                mMap.addPolygon(pOpRoSB);
                sector = 'b';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROC":
                pOpRoSC = new PolygonOptions()
                        .add(new LatLng(1.3126119,-77.4318874),
                                new LatLng(1.3113677,-77.4322093),
                                new LatLng(1.3108636,-77.4327135),
                                new LatLng(1.3092225,-77.4337113),
                                new LatLng(1.3086325,-77.4323702),
                                new LatLng(1.3095443,-77.4308038),
                                new LatLng(1.3103487,-77.4306482),
                                new LatLng(1.3105793,-77.4305838),
                                new LatLng(1.3108207,-77.4305087),
                                new LatLng(1.3109762,-77.4304926),
                                new LatLng(1.3112122,-77.4304873),
                                new LatLng(1.3114106,-77.4304497),
                                new LatLng(1.3116466,-77.4304444),
                                new LatLng(1.3119093,-77.4304229),
                                new LatLng(1.3121614,-77.430439))
                        .strokeColor(Color.MAGENTA)
                        .strokeWidth(3);

                camara = new LatLng(1.3105038256,-77.4315769032);
                mMap.addPolygon(pOpRoSC);
                sector = 'c';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROD":
                pOpRoSD = new PolygonOptions()
                        .add(new LatLng(1.3122419,-77.4304283),
                                new LatLng(1.3113918,-77.4303719),
                                new LatLng(1.3097373,-77.4289155),
                                new LatLng(1.3109708,-77.4279606),
                                new LatLng(1.3123759,-77.428894),
                                new LatLng(1.3127621,-77.4292481))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);

                camara = new LatLng(1.3112927369,-77.4290443473);
                mMap.addPolygon(pOpRoSD);
                sector = 'd';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROE":
                pOpRoSE = new PolygonOptions()
                        .add(new LatLng(1.3109708,-77.4279606),
                                new LatLng(1.309555,-77.4290335),
                                new LatLng(1.3060261,-77.4280787),
                                new LatLng(1.3069593,-77.4265873))
                        .strokeColor(Color.WHITE)
                        .strokeWidth(3);

                camara = new LatLng(1.3083330633,-77.4277680195);
                mMap.addPolygon(pOpRoSE);
                sector = 'e';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROF":
                pOpRoSF = new PolygonOptions()
                        .add(new LatLng(1.310885,-77.4270594),
                                new LatLng(1.3101556,-77.4264801),
                                new LatLng(1.3095014,-77.4250317),
                                new LatLng(1.3107563,-77.4243128),
                                new LatLng(1.3134914,-77.4244845),
                                new LatLng(1.3126655,-77.4274671))
                        .strokeColor(Color.YELLOW)
                        .strokeWidth(3);

                camara = new LatLng(1.3115973722,-77.4254578159);
                mMap.addPolygon(pOpRoSF);
                sector = 'f';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROG":
                pOpRoSG = new PolygonOptions()
                        .add(new LatLng(1.3097695,-77.4272418),
                                new LatLng(1.3078388,-77.4267375),
                                new LatLng(1.3066482,-77.4259651),
                                new LatLng(1.3071738,-77.4245274),
                                new LatLng(1.3092547,-77.424463),
                                new LatLng(1.310102,-77.427156))
                        .strokeColor(Color.LTGRAY)
                        .strokeWidth(3);

                camara = new LatLng(1.3083412288,-77.4255030401);
                mMap.addPolygon(pOpRoSG);
                sector = 'g';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector ROH":
                pOpRoSH = new PolygonOptions()
                        .add(new LatLng(1.3094155,-77.4364042),
                                new LatLng(1.3073991,-77.4356639),
                                new LatLng(1.3064873,-77.4318552),
                                new LatLng(1.3075278,-77.4314904),
                                new LatLng(1.3083537,-77.4343765),
                                new LatLng(1.3098017,-77.4349773))
                        .strokeColor(Color.GREEN)
                        .strokeWidth(3);

                camara = new LatLng(1.3079417483,-77.4337338470);
                mMap.addPolygon(pOpRoSH);
                sector = 'h';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,16));
                break;
            case "Sector ROI":
                pOpRoSI = new PolygonOptions()
                        .add(new LatLng(1.3075439,-77.4314046),
                                new LatLng(1.3059939,-77.431941),
                                new LatLng(1.3024007,-77.4245167),
                                new LatLng(1.3042885,-77.4234116),
                                new LatLng(1.3059296,-77.4246669),
                                new LatLng(1.3043636,-77.426995))
                        .strokeColor(Color.parseColor("#6C3483"))
                        .strokeWidth(3);

                camara = new LatLng(1.3053338186,-77.4272064855);
                mMap.addPolygon(pOpRoSI);
                sector = 'i';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,15));
                break;
            case "Sector TA":
                pOpTSA = new PolygonOptions()
                        .add(new LatLng(1.3576527,-77.3159033),
                                new LatLng(1.3573041,-77.3154688),
                                new LatLng(1.3572425,-77.3155144),
                                new LatLng(1.3568563,-77.3148841),
                                new LatLng(1.3567491,-77.3149699),
                                new LatLng(1.3564219,-77.3145086),
                                new LatLng(1.3570172,-77.3140794),
                                new LatLng(1.3573176,-77.3145515),
                                new LatLng(1.3579289,-77.3141116),
                                new LatLng(1.3585725,-77.3149431))
                        .strokeColor(Color.BLUE)
                        .strokeWidth(3);

                camara = new LatLng(1.3575504606,-77.3146626446);
                mMap.addPolygon(pOpTSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector TB":
                pOpTSB = new PolygonOptions()
                        .add(new LatLng(1.3564702,-77.3172873),
                                new LatLng(1.3549525,-77.3163754),
                                new LatLng(1.3564434,-77.3145783),
                                new LatLng(1.3567491,-77.3149967),
                                new LatLng(1.356851,-77.3149243),
                                new LatLng(1.3572425,-77.3155466),
                                new LatLng(1.3573041,-77.3155037),
                                new LatLng(1.3575777,-77.3158604))
                        .strokeColor(Color.CYAN)
                        .strokeWidth(3);

                camara = new LatLng(1.3563947307,-77.3158753235);
                mMap.addPolygon(pOpTSB);
                sector = 'b';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector TC":
                pOpTSC = new PolygonOptions()
                        .add(new LatLng(1.3564568,-77.3173168),
                                new LatLng(1.3570387,-77.3176575),
                                new LatLng(1.3555934,-77.3194385),
                                new LatLng(1.3551322,-77.3192346),
                                new LatLng(1.3551348,-77.3191944),
                                new LatLng(1.3539282,-77.3185319),
                                new LatLng(1.3541105,-77.3172122),
                                new LatLng(1.3549525,-77.3164022))
                        .strokeColor(Color.MAGENTA)
                        .strokeWidth(3);

                camara = new LatLng(1.3552557505,-77.3177579908);
                mMap.addPolygon(pOpTSC);
                sector = 'c';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector TD":
                pOpTSD = new PolygonOptions()
                        .add(new LatLng(1.3554834,-77.3204416),
                                new LatLng(1.3549311,-77.3211604),
                                new LatLng(1.3543411,-77.3209646),
                                new LatLng(1.3543921,-77.3207742),
                                new LatLng(1.3532525,-77.3202753),
                                new LatLng(1.3539845,-77.3185855),
                                new LatLng(1.3551161,-77.3192024),
                                new LatLng(1.3551107,-77.31924),
                                new LatLng(1.3558642,-77.3195565))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);

                camara = new LatLng(1.3545824123,-77.3198584042);
                mMap.addPolygon(pOpTSD);
                sector = 'd';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector TE":
                pOpTSE = new PolygonOptions()
                        .add(new LatLng(1.3542526,-77.3229522),
                                new LatLng(1.3532632,-77.3226196),
                                new LatLng(1.3533061,-77.3217049),
                                new LatLng(1.3527001,-77.3215038),
                                new LatLng(1.3531238,-77.3202324),
                                new LatLng(1.3543411,-77.3207742),
                                new LatLng(1.3542714,-77.3210424),
                                new LatLng(1.354856,-77.3212624),
                                new LatLng(1.3546307,-77.3224533))
                        .strokeColor(Color.WHITE)
                        .strokeWidth(3);

                camara = new LatLng(1.3536829529,-77.3215266752);
                mMap.addPolygon(pOpTSE);
                sector = 'e';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector MA":
                pOpMSA = new PolygonOptions()
                        .add(new LatLng(1.3744949,-77.3325115),
                                new LatLng(1.3737119,-77.3357946),
                                new LatLng(1.373122,-77.3365617),
                                new LatLng(1.3725804,-77.3360145),
                                new LatLng(1.3739801,-77.3322541))
                        .strokeColor(Color.BLUE)
                        .strokeWidth(3);

                camara = new LatLng(1.3740369080,-77.3344260710);
                mMap.addPolygon(pOpMSA);
                sector = 'a';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,16));
                break;
            case "Sector MB":
                pOpMSB = new PolygonOptions()
                        .add(new LatLng(1.373004,-77.3365134),
                                new LatLng(1.3727091,-77.3371196),
                                new LatLng(1.3718778,-77.337538),
                                new LatLng(1.3714649,-77.3366314),
                                new LatLng(1.3725911,-77.3360735))
                        .strokeColor(Color.CYAN)
                        .strokeWidth(3);

                camara = new LatLng(1.3722569088,-77.3367371520);
                mMap.addPolygon(pOpMSB);
                sector = 'b';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector MC":
                pOpMSC = new PolygonOptions()
                        .add(new LatLng(1.3718778,-77.337538),
                                new LatLng(1.373181,-77.3368996),
                                new LatLng(1.3739479,-77.3372805),
                                new LatLng(1.3737226,-77.3386592),
                                new LatLng(1.3724785,-77.3390025),
                                new LatLng(1.3721299,-77.3380637))
                        .strokeColor(Color.MAGENTA)
                        .strokeWidth(3);

                camara = new LatLng(1.3730214330,-77.3378576761);
                mMap.addPolygon(pOpMSC);
                sector = 'c';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
            case "Sector MD":
                pOpMSD = new PolygonOptions()
                        .add(new LatLng(1.3718778,-77.337538),
                                new LatLng(1.3709876,-77.3379564),
                                new LatLng(1.3705425,-77.3370552),
                                new LatLng(1.3710198,-77.3365831),
                                new LatLng(1.3714649,-77.3366314))
                        .strokeColor(Color.RED)
                        .strokeWidth(3);

                camara = new LatLng(1.3712122535,-77.3371690207);
                mMap.addPolygon(pOpMSD);
                sector = 'd';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector ME":
                pOpMSE = new PolygonOptions()
                        .add(new LatLng(1.3718778,-77.337538),
                                new LatLng(1.3721299,-77.3380637),
                                new LatLng(1.3723819,-77.3387825),
                                new LatLng(1.3713952,-77.3389971),
                                new LatLng(1.3709876,-77.3379564))
                        .strokeColor(Color.WHITE)
                        .strokeWidth(3);

                camara = new LatLng(1.3716908106,-77.3382078399);
                mMap.addPolygon(pOpMSE);
                sector = 'e';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector MF":
                pOpMSF = new PolygonOptions()
                        .add(new LatLng(1.3707462,-77.3374736),
                                new LatLng(1.3709876,-77.3379564),
                                new LatLng(1.3712423,-77.3385975),
                                new LatLng(1.3708428,-77.3386109),
                                new LatLng(1.370269,-77.3382273),
                                new LatLng(1.3702663,-77.3380369),
                                new LatLng(1.3700464,-77.3374978))
                        .strokeColor(Color.YELLOW)
                        .strokeWidth(3);

                camara = new LatLng(1.3705936308,-77.3379627252);
                mMap.addPolygon(pOpMSF);
                sector = 'f';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector MG":
                pOpMSG = new PolygonOptions()
                        .add(new LatLng(1.3707462,-77.3374736),
                                new LatLng(1.3700464,-77.3374978),
                                new LatLng(1.3695771,-77.3374093),
                                new LatLng(1.3695181,-77.3366958),
                                new LatLng(1.370344,-77.3367012))
                        .strokeColor(Color.LTGRAY)
                        .strokeWidth(3);

                camara = new LatLng(1.3700800572,-77.3370406273);
                mMap.addPolygon(pOpMSG);
                sector = 'g';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,18));
                break;
            case "Sector MH":
                pOpMSH = new PolygonOptions()
                        .add(new LatLng(1.370344,-77.3367012),
                                new LatLng(1.3694967,-77.3366636),
                                new LatLng(1.3695637,-77.337428),
                                new LatLng(1.3694297,-77.3377955),
                                new LatLng(1.36877,-77.3378143),
                                new LatLng(1.3689711,-77.3364061),
                                new LatLng(1.3693841,-77.3359931),
                                new LatLng(1.3701081,-77.3355049),
                                new LatLng(1.3706068,-77.3363686))
                        .strokeColor(Color.GREEN)
                        .strokeWidth(3);

                camara = new LatLng(1.3693965894,-77.3366930573);
                mMap.addPolygon(pOpMSH);
                sector = 'h';
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(camara,17));
                break;
        }
        agregarMarcador();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void backupDatabase(){
        try {
            crearDirectorioPublico("Grama");

            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            String packageName  = "com.android.gan091.gramaudenar";
            String sourceDBName = "GramaDB";
            String targetDBName = "Grama/gramaUdenar";
            if (isExternalStorageWritable()) {
                //Date now = new Date();
                String currentDBPath = "data/" + packageName + "/databases/" + sourceDBName;
                //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
                String backupDBPath = targetDBName + ".db";

                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.i("backup","backupDB=" + backupDB.getAbsolutePath());
                Log.i("backup","sourceDB=" + currentDB.getAbsolutePath());

                FileChannel src = new FileInputStream(currentDB).getChannel();
                Log.i("backup","Llegue hasta aqui");
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        } catch (Exception e) {
            Log.i("Backup", e.toString());
        }
    }

    public void generarArchivoT(){

        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos();
        archivoCad = new String[cursor.getCount()+2];//5
        archivoCad[0] = "X;" +
                "Y;" +
                "ID_CASA;" +
                "LUGAR;" +
                "T CAIDA DE CENIZA;" +
                "ESTADO GENERAL;" +
                "TIPO TECHO;" +
                "MATERIAL COBERTURA;" +
                "MATERIAL APOYO;" +
                "FORMA CUBIERTA;" +
                "INCLINACION CUBIERTA;" +
                "Ancho FG;" +
                "Alto FG;" +
                "Area FG;" +
                "Area Total Fachada;" +
                "Ancho VG;" +
                "Alto VG;" +
                "Area VG;" +
                "NPiso VG;" +
                "Ancho VP;" +
                "Alto VP;" +
                "Area VP;" +
                "NPiso VP;" +
                "Area Total Ventanas;" +
                "Ancho PG;" +
                "Alto PG;" +
                "Area PG;" +
                "Area Total Puertas;" +
                "Material Ventana;" +
                "Marco Ventana;" +
                "Material Piso;" +
                "Material Muros;" +
                "Tipologia Onda;" +
                "Tipologia Ent;" +
                "Observaciones OCH;" +
                "Tipologia Lahares;" +
                "Reforzado;Material Muros;" +
                "Estado Edificacion;" +
                "Observaciones L";

        if (cursor.moveToFirst()){
            do {
                y = degUtmLat(cursor.getDouble(1),cursor.getDouble(2),1);
                x = degUtmLat(cursor.getDouble(1),cursor.getDouble(2),2);
                archivoCad[i] =
                        Double.toString(x)+";"
                                +Double.toString(y)+";"
                                +cursor.getString(0)+";"
                                +cursor.getString(3)+";"
                                +cursor.getString(4)+";"
                                +cursor.getString(5)+";"
                                +cursor.getString(6)+";"
                                +cursor.getString(7)+";"
                                +cursor.getString(8)+";"
                                +cursor.getString(9)+";"
                                +cursor.getString(10)+";"
                                +cursor.getString(11)+";"
                                +cursor.getString(12)+";"
                                +cursor.getString(13)+";"
                                +cursor.getString(14)+";"
                                +cursor.getString(15)+";"
                                +cursor.getString(16)+";"
                                +cursor.getString(17)+";"
                                +cursor.getString(18)+";"
                                +cursor.getString(19)+";"
                                +cursor.getString(20)+";"
                                +cursor.getString(21)+";"
                                +cursor.getString(22)+";"
                                +cursor.getString(23)+";"
                                +cursor.getString(24)+";"
                                +cursor.getString(25)+";"
                                +cursor.getString(26)+";"
                                +cursor.getString(27)+";"
                                +cursor.getString(28)+";"
                                +cursor.getString(29)+";"
                                +cursor.getString(30)+";"
                                +cursor.getString(31)+";"
                                +cursor.getString(32)+";"
                                +cursor.getString(33)+";"
                                +cursor.getString(34)+";"
                                +cursor.getString(35)+";"
                                +cursor.getString(36)+";"
                                +cursor.getString(37)+";"
                                +cursor.getString(38)+";"
                                +cursor.getString(39);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Tipologias");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoF(){

        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblfachada");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "ID_CASA;ANCHO;ALTURA;AREA;AREA1";

        if (cursor.moveToFirst()){
            do {
                archivoCad[i] = cursor.getString(0)+";"+cursor.getString(1)+";"+cursor.getString(2)+";"+cursor.getString(3)+";"+cursor.getString(4);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Fachada");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoV(){

        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblventana");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "ID_CASA;ANCHO;ALTURA;AREA;NUMEROPISO";

        if (cursor.moveToFirst()){
            do {
                archivoCad[i] = cursor.getString(0)+";"+cursor.getString(1)+";"+cursor.getString(2)+";"+cursor.getString(3)+";"+cursor.getString(4);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Ventana");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoP(){

        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblpuerta");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "ID_CASA;ANCHO;ALTURA;AREA";

        if(cursor.moveToFirst()){
            do {
                archivoCad[i] = cursor.getString(0)+";"+cursor.getString(1)+";"+cursor.getString(2)+";"+cursor.getString(3);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Puerta");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoL(){

        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tbllahares");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "ID_CASA;REFORZADO;MATERIAL MUROS;ESTADO EDIFICACION;TIPOLOGIA LAHARES;OBSERVACIONES";

        if (cursor.moveToFirst()){
            do {
                boolean reforzado;
                if (cursor.getInt(1)==1){
                    reforzado = true;
                }
                else {
                    reforzado = false;
                }
                archivoCad[i] = cursor.getString(0)+";"+reforzado+";"+cursor.getString(2)+";"+cursor.getString(3)+";"+cursor.getString(4)+";"+cursor.getString(5);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"Lahares");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoOCH(){

        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblondachoque");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "ID_CASA;MATERIAL VENTANA;MARCO VENTANA;MATERIAL PISO;MATERIAL MUROS;TIPOLOGIA ONDA DE CHOQUE;TIPOLOGIA ENTERRAMIENTO;OBSERVACIONES";

        if (cursor.moveToFirst()){
            do {
                archivoCad[i] = cursor.getString(0)+";"+cursor.getString(1)+";"+cursor.getString(2)+";"+cursor.getString(3)+";"+cursor.getString(4)+";"+cursor.getString(5)+";"+cursor.getString(6)+";"+cursor.getString(7);
                i++;

            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"OndaChoqueEnterramiento");
        }
        bdP.cerrarBD();
    }

    public void generarArchivoC(){

        int i=1;
        double x,y;
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblceniza");
        archivoCad = new String[cursor.getCount()+2];
        archivoCad[0] = "ID_CASA;TIPO TECHO;MATERIAL COBERTURA;MATERIAL APOYO;FORMA CUBIERTA;INCLINACION CUBIERTA;ESTADO GENERAL;TIPOLOGIA CENIZA;OBSERVACIONES";

        if (cursor.moveToFirst()){
            do {
                archivoCad[i] = cursor.getString(0)+";"+cursor.getString(1)+";"+cursor.getString(2)+";"+cursor.getString(3)+";"+cursor.getString(4)+";"+cursor.getString(5)+";"+cursor.getString(6)+";"+cursor.getString(7)+";"+cursor.getString(8);
                i++;
            }while (cursor.moveToNext());
            guardarArchivo(archivoCad,"CaidadeCeniza");
        }
        bdP.cerrarBD();
    }

    public double degUtmLat(double deglat,double deglon, int tipo){
        System.out.println("Longitud_Decimales= "+deglon);
        System.out.println("Latitud_Decimales= "+deglat);
        double radlat=Math.toRadians(deglat);
        double radlon=Math.toRadians(deglon);
        System.out.println("Longitud_Radianes= "+radlon);
        System.out.println("Latitud_Radianes= "+radlat);
        double cos_2_radlat=0.5+((Math.cos(2*radlat))/2);
        System.out.println("Cos2radlat= "+cos_2_radlat);
        double ex=0.08199189;
        double ex_sec=0.08226889;
        double ex_2=ex_sec*ex_sec;
        System.out.println("E2= "+ex_2);
        double c=6399936.608;
        double alpha=0.003367003;
        int hus= (int) ((deglon/6)+31);
        System.out.println("Huso= "+hus);
        double lambda_0=(hus*6)-183;
        System.out.println("Lambda_0= "+lambda_0);
        double delta_lambda=radlon-(Math.toRadians(lambda_0));
        System.out.println("Delta Lambda= "+delta_lambda);
        double A=Math.cos(radlat)*Math.sin(delta_lambda);
        System.out.println("A= "+A);
        double xi=0.5*(Math.log((1+A)/(1-A)));
        System.out.println("Xi= "+xi);
        double eta=Math.atan((Math.tan(radlat)/Math.cos(delta_lambda)))-radlat;
        System.out.println("eta= "+eta);
        double ni=(c/(Math.sqrt(1+(ex_2*cos_2_radlat))))*0.9996;
        System.out.println("ni= "+ni);
        double zeta=ex_2/2*Math.pow(xi,2)*cos_2_radlat;
        System.out.println("zeta= "+zeta);
        double A_1=Math.sin(2*radlat);
        System.out.println("A_1= "+A_1);
        double A_2=A_1*cos_2_radlat;
        System.out.println("A_2= "+A_2);
        double J_2=radlat+(A_1/2);
        System.out.println("J_2= "+J_2);
        double J_4=((3*J_2)+A_2)/4;
        System.out.println("J_4= "+J_4);
        double J_6=((5*J_4)+(A_2*cos_2_radlat))/3;
        System.out.println("J_6= "+J_6);
        double alpha_1=ex_2*3/4;
        System.out.println("alpha= "+alpha_1);
        double beta= (5*Math.pow(alpha_1,2))/3;
        System.out.println("beta= "+beta);
        double gamma= (35*Math.pow(alpha_1,3))/27;
        System.out.println("gamma= "+gamma);
        double B_0=0.9996*c*(radlat-(alpha_1*J_2)+(beta*J_4)-(gamma*J_6));
        System.out.println("B_0= "+B_0);
        double utmlon=(xi*ni*(1+(zeta/3)))+500000;
        double utmlat=(eta*ni*(1+zeta))+B_0;
        System.out.println("Lat= "+utmlat+" Lon= "+utmlon);
        if(tipo==1){
            return utmlat;
        }
        else{
            return utmlon;
        }
    }

    public void guardarArchivo(String[] cad, String tabla){
        File sd = Environment.getExternalStorageDirectory();

        try {
            File folder = new File(sd.getAbsolutePath()+"/Grama");
            folder.mkdir();
            FileWriter archivo = new FileWriter(folder.getAbsolutePath()+"/tbl"+tabla+" "+spSector.getSelectedItem().toString()+".txt");

            BufferedWriter bufferedWriter = new BufferedWriter(archivo);

            for (int i=0; i<cad.length;i++){
                bufferedWriter.write(cad[i]);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
            archivo.close();
        }
        catch (Exception e){
            Log.i("Escritura",e.toString());
        }
    }

    public void updateLahares(){
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tbllahares");

        try {
            if (cursor.moveToFirst()){
                do {
                    latitud = cursor.getString(0);
                    longitud = cursor.getString(1);
                    bdP.getIdCasa(latitud,longitud);
                    //Reforzado 2
                    //Material Muros 3
                    //EstadoEdificacion 4
                    //TipologiaLahares 5
                    //Lahares 6
                    //Observaciones 7
                    if ((cursor.getString(3).equals("Madera") && (cursor.getString(4).equals("Bueno") || cursor.getString(4).equals("Regular") || cursor.getString(4).equals("Malo"))) ||
                            (cursor.getString(3).equals("Tapia Pisada") && cursor.getString(4).equals("Malo"))){
                        bdP.updateLahares(idCasa,"Tipologia_1","tbllahares");
                    }else {
                        if (((cursor.getString(3).equals("Ladrillo Macizo") || cursor.getString(3).equals("Bloque")) && cursor.getInt(2) == 0
                                && (cursor.getString(4).equals("Bueno") || cursor.getString(4).equals("Regular"))) ||
                                (cursor.getInt(2) == 1 && cursor.getString(4).equals("Malo"))){
                            bdP.updateLahares(idCasa,"Tipologia_2","tbllahares");
                        }else {
                            if ((cursor.getString(3).equals("Ladrillo Macizo") || cursor.getString(3).equals("Bloque")) && cursor.getInt(2) == 0
                            && (cursor.getString(4).equals("Bueno") || cursor.getString(4).equals("Regular"))){
                                bdP.updateLahares(idCasa,"Tipologia_3","tbllahares");
                            }else {
                                bdP.updateLahares(idCasa," ","tbllahares");
                            }
                        }
                    }
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.i("Error Lahares",e.toString());
            Toast toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        bdP.cerrarBD();
    }

    public void updateCeniza(){
        bdP.abrirBD();
        Cursor cursor = bdP.cargarDatos("tblceniza");

        try {
            if (cursor.moveToFirst()){
                do {
                    latitud = cursor.getString(0);
                    longitud = cursor.getString(1);
                    idCasa = bdP.getIdCasa(latitud,longitud);

                    //TipoTecho VARCHAR(9), 2
                    // MaterialCobertura VARCHAR(16), 3
                    // MaterialApoyo VARCHAR(16), 4
                    // FormaCubierta VARCHAR(13), 5
                    // InclinacionCubierta VARCHAR(20), 6
                    // EstadoGeneralCubierta VARCHAR(8), 7
                    // TipologiaCeniza VARCHAR(12), 8
                    // ObservacionesC VARCHAR(200) 9

                    if (((cursor.getString(3).equals("Teja Eternit") || cursor.getString(3).equals("Teja Zinc") || cursor.getString(3).equals("Policarbonato") || cursor.getString(3).equals("Teja Traslucida")) &&
                            (cursor.getString(7).equals("Bueno") || cursor.getString(7).equals("Regular") || cursor.getString(7).equals("Malo"))) ||
                            (cursor.getString(3).equals("Teja Barro") && cursor.getString(7).equals("Malo"))){
                        bdP.updateCeniza(idCasa,"Tipologia_1","tblceniza");
                    }else {
                        if (cursor.getString(3).equals("Teja Barro") && (cursor.getString(7).equals("Bueno") || cursor.getString(7).equals("Regular"))){
                            bdP.updateCeniza(idCasa,"Tipologia_2","tblceniza");
                        }else {
                            if (cursor.getString(2).equals("Losas")){
                                bdP.updateCeniza(idCasa,"Tipologia_3","tblceniza");
                            }else {
                                bdP.updateCeniza(idCasa," ","tblceniza");
                            }
                        }
                    }
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.i("Error Ceniza",e.toString());
            Toast toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        bdP.cerrarBD();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_settings:
                i = new Intent(MapsActivity.this, AcercadeActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.left_in,R.anim.left_out);
                break;
            case R.id.action_tutorial:
                i = new Intent(MapsActivity.this, TutorialActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.left_in,R.anim.left_out);
                break;
            case R.id.action_exit:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Opciones del panel lateral

        switch (menuItem.getItemId()){
            case R.id.nav_registerUser:
                Intent i = new Intent(MapsActivity.this, RegistrarUsuarios.class);
                startActivity(i);;
                break;
            case R.id.nav_showUser:
                Intent i2 = new Intent(MapsActivity.this, MostrarUsuario.class);
                startActivity(i2);
                break;
            case R.id.nav_loginUser:
                LayoutInflater layoutInflater = getLayoutInflater();
                View dialogLayout = layoutInflater.inflate(R.layout.form_login,null);

                final EditText etFUser = dialogLayout.findViewById(R.id.etFormUser);
                final EditText etFPassword = dialogLayout.findViewById(R.id.etFormPassword);

                Button btnFLAceptar = dialogLayout.findViewById(R.id.btnFormLoginAceptar);
                Button btnFLCancelar = dialogLayout.findViewById(R.id.btnFormLoginCancelar);

                final AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setView(dialogLayout);
                d = builder.create();

                btnFLAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (TextUtils.isEmpty(etFUser.getText().toString())){
                            etFUser.requestFocus();
                            etFUser.setError("El nombre de usuario no puede quedar vacio");
                            return;
                        }
                        else{
                            if(TextUtils.isEmpty(etFPassword.getText().toString())){
                                etFPassword.requestFocus();
                                etFPassword.setError("La contraseña no puede quedar vacia");
                                return;
                            }
                            else {
                                String user = etFUser.getText().toString();
                                String password = etFPassword.getText().toString();
                                if (gesUs.login(user,password) >= 1){
                                    navigationView.getMenu().findItem(R.id.nav_loginUser).setVisible(false);
                                    navigationView.getMenu().findItem(R.id.nav_registerUser).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_showUser).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_closeLogin).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_camera).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_backup).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_extraRecords).setVisible(true);
                                    navigationView.getMenu().findItem(R.id.nav_updateRecords).setVisible(true);
                                    spSector.setVisibility(View.VISIBLE);
                                    spCorregimiento.setVisibility(View.VISIBLE);
                                    btnActualizar.setVisibility(View.VISIBLE);

                                    Snackbar.make(navigationView.getHeaderView(0), "Iniciaste sesión correctamente, revisa las funciones disponibles", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                                else{
                                    Snackbar.make(view, "Contraseña y/o Usuario invalido", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    return;
                                }
                            }
                        }
                        d.dismiss();
                    }
                });

                btnFLCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        d.dismiss();
                    }
                });

                d.show();
                break;
            case R.id.nav_closeLogin:
                navigationView.getMenu().findItem(R.id.nav_loginUser).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_registerUser).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_showUser).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_closeLogin).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_camera).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_backup).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_extraRecords).setVisible(false);
                navigationView.getMenu().findItem(R.id.nav_updateRecords).setVisible(false);
                spSector.setVisibility(View.INVISIBLE);
                spCorregimiento.setVisibility(View.INVISIBLE);
                btnActualizar.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_camera:
                fijarZona();
                break;
            case R.id.nav_backup:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
                }else {
                    backup();
                }
                break;
            case R.id.nav_extraRecords:
                generarInfExtra();
                break;
            case R.id.nav_updateRecords:
                updateCeniza();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public File crearDirectorioPublico(String nombreDirectorio) {
        //Crear directorio público en la carpeta Pictures.
        File directorio = new File(Environment.getExternalStorageDirectory(), nombreDirectorio);
        //Muestro un mensaje en el logcat si no se creo la carpeta por algun motivo
        if (!directorio.mkdirs())
            Log.e("backup", "Error: No se creo el directorio público");

        return directorio;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            backup();
        }else {
            Toast.makeText(MapsActivity.this, "Permisos denegados, no se pudo realizar la exportación de los datos", Toast.LENGTH_SHORT).show();
        }
    }

    public void backup(){
        generarArchivoT();
        generarArchivoF();
        generarArchivoV();
        generarArchivoP();
        generarArchivoOCH();
        generarArchivoL();
        generarArchivoC();
        backupDatabase();

        Toast t = Toast.makeText(this,"Exportacion de Archivos completada",Toast.LENGTH_SHORT);
        t.show();
    }
}