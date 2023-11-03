package com.example.seyahat_rehber;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import Interface.ItemClickListener;
import ViewHolder.BolgeViewHolder;
import info.hoang8f.widget.FButton;
import model.Bolgeler;

public class MainActivity extends AppCompatActivity {
    Button btn_bolge_ekle;
    MaterialEditText edtBolgeAdi;
    FButton btnSec,btnYukle;
    public static final int PICK_IMAGE_REQUEST=71;
    Uri kaydetmeUrisi;

    private DatabaseReference bolgeYolu;
    private StorageReference resimYolu;

     //Modelim
    Bolgeler yeniBolge;

    //RecyclerView
    FirebaseRecyclerAdapter<Bolgeler, BolgeViewHolder> adapter;
    RecyclerView recycler_bolge;
    RecyclerView.LayoutManager layoutManager; // arka plan yerleşimini ayarlamak için

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView
        recycler_bolge=findViewById(R.id.recyler_bolgeler);
        recycler_bolge.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_bolge.setLayoutManager(layoutManager);



        // Firebase
        //Firebase tanımlamaları
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        bolgeYolu= database.getReference("Bolge");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        resimYolu= storage.getReference();

        btn_bolge_ekle=findViewById(R.id.btn_bolge_ekle);
        btn_bolge_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bolgeEklemePenceresiGoster();
            }
        });
        
        bolgeYukle();
        }

    private void bolgeYukle() {
       FirebaseRecyclerOptions<Bolgeler> secenekler=new FirebaseRecyclerOptions.Builder<Bolgeler>()
               .setQuery(bolgeYolu,Bolgeler.class)
               .build();

        adapter=new FirebaseRecyclerAdapter<Bolgeler,BolgeViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull BolgeViewHolder holder, int position, @NonNull Bolgeler model) {
                holder.txtBolgeAdi.setText(model.getAd());
                Picasso.with(getBaseContext()).load(model.getResim()).into(holder.imageView);
               final Bolgeler tiklandiginda=model;

               holder.setItemClickListener((view, position1, isLongClick) -> {
                   Intent sehir=new Intent(MainActivity.this,SehirlerActivity.class);
                   sehir.putExtra("BolgeId",adapter.getRef(position).getKey());
                   startActivity(sehir);

               });
            }

            @NonNull
            @Override
            public BolgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.bolge_satiri_ogesi,parent,false);
                return new BolgeViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_bolge.setAdapter(adapter);


    }

    private void bolgeEklemePenceresiGoster() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Yeni Bolge Ekle");
        builder.setMessage("Lütfen bilgilerinizi yazın");
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View yeni_bolge_ekleme_penceresi=layoutInflater.inflate(R.layout.yeni_bolge_ekleme_penceresi,null);

        edtBolgeAdi=yeni_bolge_ekleme_penceresi.findViewById(R.id.edt_bolge_adi);
        btnSec=yeni_bolge_ekleme_penceresi.findViewById(R.id.btnSec);
        btnYukle=yeni_bolge_ekleme_penceresi.findViewById(R.id.btnYukle);

        btnSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resimSec();
            }
        });
        btnYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resimYukle();
            }
        });

        builder.setView(yeni_bolge_ekleme_penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which){

                if(yeniBolge!=null)
                {
                    bolgeYolu.push().setValue((yeniBolge)); //veritabanı yolumu push ettim
                    Toast.makeText(MainActivity.this, yeniBolge.getAd()+"bölgesi eklendi", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("VAZGEÇ",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();

            }
        });
        builder.show();
        }

    private void resimSec() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        Intent data = result.getData();
                        kaydetmeUrisi=data.getData();
                        btnSec.setText("SECİLDİ");

                    }
                }

            });


    private void resimYukle() {
        if(kaydetmeUrisi!=null)
        {
            ProgressDialog mDialog=new ProgressDialog(this);
            mDialog.setMessage("Yükleniyor");
            mDialog.show();

            //Firebase de storage'e atacak
            String resimAdi= UUID.randomUUID().toString();
            StorageReference resimDosyasi=resimYolu.child("resimler/"+resimAdi); //İçine çocuk açıyor
            resimDosyasi.putFile(kaydetmeUrisi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Resim Yüklendi", Toast.LENGTH_SHORT).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //resmin veri tabanına aktarıldığı yer
                            yeniBolge=new Bolgeler(edtBolgeAdi.getText().toString(),uri.toString());
                            //resmi bölgeler modeline aktartıyoruz bu modeli de veritabanına göndermeliyiz builder.setpositivebuttonla
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    mDialog.setMessage("%d"+progress+"yüklendi");
                }
            });
        }
    }

    public boolean onContextItemSelected(MenuItem item)
    {
        if(item.getTitle().equals("Sil"))
        {
            bolgeSil(adapter.getRef(item.getOrder()).getKey());
        }
        else if(item.getTitle().equals("Güncelle"))
        {
            bolgeGuncellemePenceresiGoster(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void bolgeGuncellemePenceresiGoster(String key, Bolgeler item) {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Yeni Bolge Ekle");
        builder.setMessage("Lütfen bilgilerinizi yazın");
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View yeni_bolge_ekleme_penceresi=layoutInflater.inflate(R.layout.yeni_bolge_ekleme_penceresi,null);

        edtBolgeAdi=yeni_bolge_ekleme_penceresi.findViewById(R.id.edt_bolge_adi);
        btnSec=yeni_bolge_ekleme_penceresi.findViewById(R.id.btnSec);
        btnYukle=yeni_bolge_ekleme_penceresi.findViewById(R.id.btnYukle);

        btnSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resimSec();
            }
        });
        btnYukle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resimDegis(item);
            }
        });

        builder.setView(yeni_bolge_ekleme_penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("GÜNCELLE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which){
                item.setAd(edtBolgeAdi.getText().toString());
                bolgeYolu.child(key).setValue(item);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("VAZGEÇ",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //sonra kodlanacak
                dialog.dismiss();

            }
        });
        builder.show();
    }

    private void resimDegis(Bolgeler item) {
        if(kaydetmeUrisi!=null)
        {
            ProgressDialog mDialog=new ProgressDialog(this);
            mDialog.setMessage("Yükleniyor");
            mDialog.show();

            //Firebase de storage'e atacak
            String resimAdi= UUID.randomUUID().toString();
            StorageReference resimDosyasi=resimYolu.child("resimler/"+resimAdi); //İçine çocuk açıyor
            resimDosyasi.putFile(kaydetmeUrisi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Resim Güncellendi", Toast.LENGTH_SHORT).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //resmin veri tabanına aktarıldığı yer

                            item.setResim(uri.toString());
                            yeniBolge=new Bolgeler(edtBolgeAdi.getText().toString(),uri.toString());
                            //resmi bölgeler modeline aktartıyoruz bu modeli de veritabanına göndermeliyiz builder.setpositivebuttonla
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    mDialog.setMessage("%d"+progress+"yüklendi");
                }
            });
        }
    }


    private void bolgeSil(String key) {
        bolgeYolu.child(key).removeValue();
    }


}









