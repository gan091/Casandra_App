package com.android.gan091.gramaudenar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;

public class FachadaActivity extends AppCompatActivity implements View.OnClickListener {

    //Intancia de variables a utilizar
    private ColorDrawable swipeBackground = new ColorDrawable(Color.parseColor("#EB1E1E"));
    private ColorDrawable swipeBackground2 = new ColorDrawable(Color.parseColor("#2196F3"));
    private Drawable iconDelete, iconUpdate;

    boolean existe = false;
    float alto, area,areaPiso1;
    int idCasa, numeroPisos, posicion;
    long idRegistro = 0;

    Fuente objetoEliminado;
    Cursor cursorEliminado;
    AlertDialog d = null;
    AdaptadorFachada adaptador;
    ArrayList<Fuente> lista = new ArrayList<>();
    BaseDeDatos bdP;
    Context context;
    EditText etAltura;
    RecyclerView contenedor;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.right_in,R.anim.right_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fachada);
        Toolbar toolbar = findViewById(R.id.toolbarF);
        setSupportActionBar(toolbar);
        FloatingActionButton fabAdd = findViewById(R.id.fabAddFachada);
        FloatingActionButton fabExit = findViewById(R.id.fabExitFachada);
        Bundle bundle = getIntent().getExtras();

        idCasa = bundle.getInt("idCasa");
        context = this;
        bdP = new BaseDeDatos(context);

        etAltura = findViewById(R.id.etAltura);

        contenedor = findViewById(R.id.contenedorFachada);
        contenedor.setHasFixedSize(true);
        LinearLayoutManager layout = new LinearLayoutManager(getApplicationContext());
        layout.setOrientation(LinearLayoutManager.VERTICAL);

        cargarDatos();

        adaptador = new AdaptadorFachada(lista);
        contenedor.setAdapter(adaptador);
        contenedor.setLayoutManager(layout);

        iconDelete = ContextCompat.getDrawable(this,R.drawable.ic_delete);
        iconUpdate = ContextCompat.getDrawable(this,R.drawable.ic_update);

        fabAdd.setOnClickListener(this);
        fabExit.setOnClickListener(this);

        loadSwipe();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.fabAddFachada:
                actualizarLista();
                break;
            case R.id.fabExitFachada:
                onBackPressed();
                break;
        }
    }

    public void cargarDatos(){

        bdP.abrirBD();
        final Cursor cursor = bdP.cargarDatos("ancho","altura", "ROWID","tblfachada",idCasa);

        try {
            if (cursor.moveToFirst()){

                existe = true;
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);

                builder1.setTitle("Registro Existente")
                        .setMessage("Ya hay registros de Fachada de esta casa ¿Desea modificar los registros?")
                        .setPositiveButton("Si",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        inicializar(cursor);
                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                builder1.create().show();
            }
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        bdP.cerrarBD();
    }

    public void inicializar(Cursor cursor){

        float ancho;

        do {
            ancho = cursor.getFloat(0);
            alto = cursor.getFloat(1);
            idRegistro = cursor.getLong(2);
            lista.add(new Fuente(ancho,idRegistro));
            //Log.i("Ancho-->",String.valueOf(ancho));
            //Log.i("RowID-->",String.valueOf(idRegistro));
        }while (cursor.moveToNext());

        numeroPisos = (int) (alto/2.8)+1;//Verificar si se debe eliminar el 1 que se suma
        etAltura.setText(Integer.toString(numeroPisos));
        adaptador.notifyDataSetChanged();
    }

    //Opciones al deslizar tarjetas
    public void loadSwipe(){

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                int iconMargin = (itemView.getHeight() - iconDelete.getIntrinsicHeight()) / 2;
                if (dX>0){
                    swipeBackground.setBounds(
                            itemView.getLeft(),
                            itemView.getTop(),
                            (int)dX,
                            itemView.getBottom());
                    iconDelete.setBounds(
                            itemView.getLeft()+iconMargin,
                            itemView.getTop()+iconMargin,
                            itemView.getLeft()+iconMargin+iconDelete.getIntrinsicWidth(),
                            itemView.getBottom()-iconMargin);
                }else {
                    swipeBackground2.setBounds(
                            itemView.getRight()+(int)dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom());
                    iconUpdate.setBounds(
                            itemView.getRight()-iconMargin-iconDelete.getIntrinsicWidth(),
                            itemView.getTop()+iconMargin,
                            itemView.getRight()-iconMargin,
                            itemView.getBottom()-iconMargin);
                }

                swipeBackground.draw(c);
                swipeBackground2.draw(c);

                c.save();

                if(dX > 0){
                    c.clipRect(
                            itemView.getLeft(),
                            itemView.getTop(),
                            (int)dX,
                            itemView.getBottom());
                }else {
                    c.clipRect(
                            itemView.getRight()+(int)dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom());
                }

                iconDelete.draw(c);
                iconUpdate.draw(c);

                c.restore();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //direccion = 4 --> Izquierda direccion = 8 --> Derecha

                posicion = viewHolder.getAdapterPosition();
                objetoEliminado = lista.get(posicion);

                idRegistro = lista.get(posicion).getIdRegistro();
                bdP.abrirBD();
                cursorEliminado = bdP.cargarDatos(idRegistro,"tblFachada");
                cursorEliminado.moveToFirst();
                bdP.cerrarBD();

                //Log.i("RowID a eliminar-->",String.valueOf(idRegistro));
                //Log.i("Dirección-->",String.valueOf(direction));

                if (direction == 8){
                    try {
                        bdP.eliminarRegistroExacto("tblFachada",idRegistro);
                    }
                    catch (Exception e){
                        Toast toast;
                        toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                        toast.show();
                    }
                    lista.remove(posicion);
                    adaptador.notifyDataSetChanged();

                    Snackbar.make(viewHolder.itemView,"Registro eliminado",Snackbar.LENGTH_LONG).setAction("Deshacer", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                long registro = bdP.insertarFachada(
                                        cursorEliminado.getLong(0), //RowId
                                        cursorEliminado.getInt(1),  //IdCasa
                                        cursorEliminado.getFloat(2),//Ancho
                                        cursorEliminado.getFloat(3),//Alto
                                        cursorEliminado.getFloat(4),//Area
                                        cursorEliminado.getFloat(5));//Area Piso 1

                                //Log.i("RowID recuperado-->",String.valueOf(registro));
                            }
                            catch (Exception e){
                                Toast toast;
                                toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                                toast.show();
                            }
                            lista.add(posicion,objetoEliminado);
                            adaptador.notifyDataSetChanged();
                        }
                    }).show();
                }else {
                    actualizarRegistro(idRegistro,objetoEliminado.getAncho(),objetoEliminado);
                }
                adaptador.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(contenedor);
    }

    public long obtenerArea(float aFachada) {

        long rFachada=0;

        //Calcula el area acumulativa de la Fachada

        numeroPisos = Integer.parseInt(etAltura.getText().toString());
        alto = (float) (numeroPisos*2.8);
        area = aFachada * alto;
        areaPiso1 = (float)(aFachada * 2.8);

        try {
            rFachada = bdP.insertarFachada(idCasa,aFachada,alto,area,areaPiso1);
        }
        catch (Exception e){
            Toast toast;
            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
            toast.show();
        }
        return rFachada;
    }

    public void actualizarLista(){
        if (TextUtils.isEmpty(etAltura.getText().toString())){
            etAltura.requestFocus();
            etAltura.setError("Antes de agregar el ancho de la Fachada se debe ingresar el numero de pisos de la vivienda");
            return;
        }
        else {
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogLayout = layoutInflater.inflate(R.layout.form_fachada,null);

            final EditText etFFAncho = (EditText) dialogLayout.findViewById(R.id.etFormAncho);
            final EditText etFFCantidad = (EditText) dialogLayout.findViewById(R.id.etFormCantidad);

            Button btnFFAceptar = (Button) dialogLayout.findViewById(R.id.btnFormFAceptar);
            Button btnFFCancelar = (Button) dialogLayout.findViewById(R.id.btnFormFCancelar);

            final AlertDialog.Builder builder = new AlertDialog.Builder(FachadaActivity.this);

            builder.setView(dialogLayout);
            d = builder.create();

            btnFFAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int cantidad = Integer.parseInt(etFFCantidad.getText().toString());

                    if (TextUtils.isEmpty(etFFAncho.getText().toString())){
                        etFFAncho.requestFocus();
                        etFFAncho.setError("El ancho de fachada no puede quedar vacio");
                        return;
                    }
                    else{
                        float anchoRegistro = Float.parseFloat(etFFAncho.getText().toString());

                        for (int i=0;i<cantidad;i++){
                            long registro = obtenerArea(anchoRegistro);
                            //Log.i("RowID Guardar-->",String.valueOf(registro));
                            lista.add(new Fuente(anchoRegistro,registro));
                        }
                    }
                    adaptador.notifyDataSetChanged();
                    d.dismiss();
                }
            });

            btnFFCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    d.dismiss();
                }
            });

            d.show();
        }
    }

    public void actualizarRegistro(final long idR, float ancho, final Fuente oE){

        numeroPisos = Integer.parseInt(etAltura.getText().toString());
        alto = (float) (numeroPisos*2.8);

        if (TextUtils.isEmpty(etAltura.getText().toString())){
            etAltura.requestFocus();
            etAltura.setError("Antes de agregar el ancho de la Fachada se debe ingresar el numero de pisos de la vivienda");
            return;
        }
        else {
            LayoutInflater layoutInflater = getLayoutInflater();
            View dialogLayout = layoutInflater.inflate(R.layout.form_fachada,null);

            final EditText etFFAncho = (EditText) dialogLayout.findViewById(R.id.etFormAncho);
            final EditText etFFCantidad = (EditText) dialogLayout.findViewById(R.id.etFormCantidad);

            Button btnFFAceptar = (Button) dialogLayout.findViewById(R.id.btnFormFAceptar);
            Button btnFFCancelar = (Button) dialogLayout.findViewById(R.id.btnFormFCancelar);

            final AlertDialog.Builder builder = new AlertDialog.Builder(FachadaActivity.this);
            builder.setView(dialogLayout);
            d = builder.create();

            etFFAncho.setText(Float.toString(ancho));
            etFFCantidad.setVisibility(View.INVISIBLE);
            btnFFAceptar.setText("Actualizar");

            btnFFAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (TextUtils.isEmpty(etFFAncho.getText().toString())){
                        etFFAncho.requestFocus();
                        etFFAncho.setError("El ancho de fachada no puede quedar vacio");
                        return;
                    }
                    else{
                        float anchoRegistro = Float.parseFloat(etFFAncho.getText().toString());
                        area = anchoRegistro * alto;
                        areaPiso1 = (float)(anchoRegistro * 2.8);

                        bdP.abrirBD();
                        try {
                            bdP.updateFachada(idR,anchoRegistro,area,areaPiso1);
                        }
                        catch (Exception e){
                            Toast toast;
                            toast=Toast.makeText(context,e.toString(),Toast.LENGTH_LONG);
                            toast.show();
                        }
                        bdP.cerrarBD();

                        oE.setAncho(anchoRegistro);
                    }
                    adaptador.notifyDataSetChanged();
                    d.dismiss();
                }
            });

            btnFFCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    d.dismiss();
                }
            });

            d.show();
        }
    }
}