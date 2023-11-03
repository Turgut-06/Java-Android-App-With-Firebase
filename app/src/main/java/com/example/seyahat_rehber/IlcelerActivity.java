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

import java.util.IllegalFormatCodePointException;
import java.util.UUID;

import ViewHolder.BolgeViewHolder;
import ViewHolder.IlceViewHolder;
import ViewHolder.SehirViewHolder;
import info.hoang8f.widget.FButton;
import model.Bolgeler;
import model.Ilceler;
import model.Sehirler;

public class IlcelerActivity extends AppCompatActivity {


    MaterialEditText edtIlceAdi;
    FButton btnSec,btnYukle;
    public static final int PICK_IMAGE_REQUEST=71;
    Uri kaydetmeUrisi;

    private DatabaseReference ilceYolu;
    private StorageReference resimYolu;

    String sehirId="";
    //Modelim
    Ilceler yeniIlce;

    //RecyclerView
    FirebaseRecyclerAdapter<Ilceler, IlceViewHolder> adapter;
    RecyclerView recycler_ilce;
    RecyclerView.LayoutManager layoutManager; // arka plan yerleşimini ayarlamak için
    Button btn_ilce_ekle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ilceler);

        //RecyclerView
        recycler_ilce= findViewById(R.id.recyler_ilceler);
        recycler_ilce.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_ilce.setLayoutManager(layoutManager);

        //Firebase tanımlamaları
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        ilceYolu= database.getReference("Ilceler");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        resimYolu= storage.getReference();



        if(getIntent()!=null)
        {
            sehirId=getIntent().getStringExtra("SehirId");
        }
        if(sehirId!=null)
            IlceleriYukle(sehirId);

        btn_ilce_ekle=findViewById(R.id.btn_ilce_ekle);
        btn_ilce_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ilceEklemePenceresiGoster();
            }
        });
    }

    private void ilceEklemePenceresiGoster() {
        AlertDialog.Builder builder=new AlertDialog.Builder(IlcelerActivity.this);
        builder.setTitle("Yeni İlçe Ekle");
        builder.setMessage("Lütfen bilgilerinizi yazın");
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View yeni_ilce_ekleme_penceresi=layoutInflater.inflate(R.layout.yeni_ilce_ekleme_penceresi,null);

        edtIlceAdi=yeni_ilce_ekleme_penceresi.findViewById(R.id.edt_ilce_adi);
        btnSec=yeni_ilce_ekleme_penceresi.findViewById(R.id.btnSec);
        btnYukle=yeni_ilce_ekleme_penceresi.findViewById(R.id.btnYukle);

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

        builder.setView(yeni_ilce_ekleme_penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which){

                if(yeniIlce!=null)
                {
                    ilceYolu.push().setValue((yeniIlce)); //veritabanı yolumu push ettim
                    Toast.makeText(IlcelerActivity.this, yeniIlce.getAd()+"/t şehri eklendi", Toast.LENGTH_SHORT).show();
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

    private void IlceleriYukle(String sehirId) {
        Query filtrele = ilceYolu.orderByChild("sehirId").equalTo(sehirId);
        FirebaseRecyclerOptions<Ilceler> secenekler=new FirebaseRecyclerOptions.Builder<Ilceler>()
                .setQuery(filtrele,Ilceler.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Ilceler, IlceViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull IlceViewHolder holder, int position, @NonNull Ilceler model) {
                holder.txtIlceAdi.setText(model.getAd());
                Picasso.with(getBaseContext()).load(model.getResim()).into(holder.imageView);
                final Ilceler tiklandiginda = model;
                holder.setItemClickListener((view, position1, isLongClick) -> {


                        Intent yerler=new Intent(IlcelerActivity.this,YerlerActivity.class);
                        yerler.putExtra("ilceId",adapter.getRef(position).getKey());
                        startActivity(yerler);

                    });

            }
            @NonNull
            @Override
            public IlceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.ilce_satiri_ogesi,parent,false);
                return new IlceViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_ilce.setAdapter(adapter);
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
                    Toast.makeText(IlcelerActivity.this, "Resim Yüklendi", Toast.LENGTH_SHORT).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //resmin veri tabanına aktarıldığı yer
                            yeniIlce=new Ilceler(edtIlceAdi.getText().toString(),uri.toString(),sehirId);
                            //resmi şehirler modeline aktartıyoruz bu modeli de veritabanına göndermeliyiz builder.setpositivebuttonla
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(IlcelerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
            ilceSil(adapter.getRef(item.getOrder()).getKey());
        }
        else if(item.getTitle().equals("Güncelle"))
        {
            ilceGuncellemePenceresiGoster(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void ilceGuncellemePenceresiGoster(String key, Ilceler item) {
        AlertDialog.Builder builder=new AlertDialog.Builder(IlcelerActivity.this);
        builder.setTitle("Yeni İlçe Ekle");
        builder.setMessage("Lütfen bilgilerinizi yazın");
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View yeni_ilce_ekleme_penceresi=layoutInflater.inflate(R.layout.yeni_ilce_ekleme_penceresi,null);

        edtIlceAdi=yeni_ilce_ekleme_penceresi.findViewById(R.id.edt_ilce_adi);
        btnSec=yeni_ilce_ekleme_penceresi.findViewById(R.id.btnSec);
        btnYukle=yeni_ilce_ekleme_penceresi.findViewById(R.id.btnYukle);

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

        builder.setView(yeni_ilce_ekleme_penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("GÜNCELLE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which){
                item.setAd(edtIlceAdi.getText().toString());
                ilceYolu.child(key).setValue(item);
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

    private void resimDegis(Ilceler item) {
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
                    Toast.makeText(IlcelerActivity.this, "Resim Güncellendi", Toast.LENGTH_SHORT).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //resmin veri tabanına aktarıldığı yer

                            item.setResim(uri.toString());
                            yeniIlce=new Ilceler(edtIlceAdi.getText().toString(),uri.toString());
                            //resmi şehirler modeline aktartıyoruz bu modeli de veritabanına göndermeliyiz builder.setpositivebuttonla
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(IlcelerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void ilceSil(String key) {
        ilceYolu.child(key).removeValue();
    }

}