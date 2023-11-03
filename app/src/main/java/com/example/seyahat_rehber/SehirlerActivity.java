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
import android.app.DownloadManager;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import Interface.ItemClickListener;
import ViewHolder.BolgeViewHolder;
import ViewHolder.SehirViewHolder;
import info.hoang8f.widget.FButton;
import model.Bolgeler;
import model.Sehirler;

public class SehirlerActivity extends AppCompatActivity {

     Button btn_sehir_ekle;
    MaterialEditText edtSehirAdi;
     FButton btnSec,btnYukle;
    public static final int PICK_IMAGE_REQUEST=71;
    Uri kaydetmeUrisi;


    //Modelim
    Sehirler yeniSehir;

    String bolgeId="";

    private DatabaseReference sehirYolu;
   private StorageReference resimYolu;

    //RecyclerView
    FirebaseRecyclerAdapter<Sehirler, SehirViewHolder> adapter;
    RecyclerView recycler_sehirler;
    RecyclerView.LayoutManager layoutManager; // arka plan yerleşimini ayarlamak için

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sehirler);

        //RecyclerView
        recycler_sehirler= findViewById(R.id.recyler_sehirler);
        recycler_sehirler.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_sehirler.setLayoutManager(layoutManager);

        //Firebase tanımlamaları
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        sehirYolu= database.getReference("Sehirler");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        resimYolu= storage.getReference();



        if(getIntent()!=null)
        {
            bolgeId=getIntent().getStringExtra("BolgeId");
        }
        if(bolgeId!=null)
            sehirleriYukle(bolgeId);








        btn_sehir_ekle=findViewById(R.id.btn_sehir_ekle);
        btn_sehir_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sehirEklemePenceresiGoster();
            }
        });
    }

    private void sehirleriYukle(String bolgeId) {
        
        Query filtrele = sehirYolu.orderByChild("bolgeId").equalTo(bolgeId);
        FirebaseRecyclerOptions<Sehirler> secenekler=new FirebaseRecyclerOptions.Builder<Sehirler>()
                .setQuery(filtrele,Sehirler.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Sehirler,SehirViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull SehirViewHolder holder, int position, @NonNull Sehirler model) {
                holder.txtSehirAdi.setText(model.getAd());
                Picasso.with(getBaseContext()).load(model.getResim()).into(holder.imageView);
                final Sehirler tiklandiginda = model;
                holder.setItemClickListener((view, position1, isLongClick) ->  {


                        Intent ilceler=new Intent(SehirlerActivity.this,IlcelerActivity.class);
                        ilceler.putExtra("SehirId",adapter.getRef(position).getKey());
                        startActivity(ilceler);

                });
            }
            @NonNull
            @Override
            public SehirViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.sehir_satiri_ogesi,parent,false);
                return new SehirViewHolder(itemView);
            }



        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_sehirler.setAdapter(adapter);
    }

    private void sehirEklemePenceresiGoster() {
        AlertDialog.Builder builder=new AlertDialog.Builder(SehirlerActivity.this);
        builder.setTitle("Yeni Sehir Ekle");
        builder.setMessage("Lütfen bilgilerinizi yazın");
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View yeni_sehir_ekleme_penceresi=layoutInflater.inflate(R.layout.yeni_sehir_ekleme_penceresi,null);

        edtSehirAdi=yeni_sehir_ekleme_penceresi.findViewById(R.id.edt_sehir_adi);
        btnSec=yeni_sehir_ekleme_penceresi.findViewById(R.id.btnSec);
        btnYukle=yeni_sehir_ekleme_penceresi.findViewById(R.id.btnYukle);

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

        builder.setView(yeni_sehir_ekleme_penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which){

                if(yeniSehir!=null)
                {
                    sehirYolu.push().setValue((yeniSehir)); //veritabanı yolumu push ettim
                    Toast.makeText(SehirlerActivity.this, yeniSehir.getAd()+"/t şehri eklendi", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SehirlerActivity.this, "Resim Yüklendi", Toast.LENGTH_SHORT).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //resmin veri tabanına aktarıldığı yer
                           yeniSehir=new Sehirler(edtSehirAdi.getText().toString(),uri.toString(),bolgeId);
                             //resmi şehirler modeline aktartıyoruz bu modeli de veritabanına göndermeliyiz builder.setpositivebuttonla
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(SehirlerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
            sehirSil(adapter.getRef(item.getOrder()).getKey());
        }
        else if(item.getTitle().equals("Güncelle"))
        {
            sehirGuncellemePenceresiGoster(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void sehirGuncellemePenceresiGoster(String key, Sehirler item) {
        AlertDialog.Builder builder=new AlertDialog.Builder(SehirlerActivity.this);
        builder.setTitle("Yeni Sehir Ekle");
        builder.setMessage("Lütfen bilgilerinizi yazın");
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View yeni_sehir_ekleme_penceresi=layoutInflater.inflate(R.layout.yeni_sehir_ekleme_penceresi,null);

        edtSehirAdi=yeni_sehir_ekleme_penceresi.findViewById(R.id.edt_sehir_adi);
        btnSec=yeni_sehir_ekleme_penceresi.findViewById(R.id.btnSec);
        btnYukle=yeni_sehir_ekleme_penceresi.findViewById(R.id.btnYukle);

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

        builder.setView(yeni_sehir_ekleme_penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("GÜNCELLE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which){
                item.setAd(edtSehirAdi.getText().toString());
                sehirYolu.child(key).setValue(item);
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

    private void resimDegis(Sehirler item) {
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
                    Toast.makeText(SehirlerActivity.this, "Resim Güncellendi", Toast.LENGTH_SHORT).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //resmin veri tabanına aktarıldığı yer

                            item.setResim(uri.toString());
                            yeniSehir=new Sehirler(edtSehirAdi.getText().toString(),uri.toString());
                            //resmi şehirler modeline aktartıyoruz bu modeli de veritabanına göndermeliyiz builder.setpositivebuttonla
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(SehirlerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void sehirSil(String key) {
        sehirYolu.child(key).removeValue();
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
                        // There are no request codes
                        Intent data = result.getData();
                        kaydetmeUrisi=data.getData();
                        btnSec.setText("SECİLDİ");

                    }
                }

            });
}