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
import android.widget.TextView;
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

import ViewHolder.IlceViewHolder;
import ViewHolder.YerViewHolder;
import info.hoang8f.widget.FButton;
import model.Ilceler;
import model.Yerler;

public class YerlerActivity extends AppCompatActivity {

    TextView yer_izleme_id;
    Button btn_izle,btn_yer_ekle;
    MaterialEditText edtYerAdi;
    MaterialEditText edtTanıtıcıMetin;
    MaterialEditText edtKonum;
    MaterialEditText edtIzlemeLinki;
    FButton btnSec,btnYukle;
    public static final int PICK_IMAGE_REQUEST=71;
    Uri kaydetmeUrisi;

    private DatabaseReference yerYolu;
    private StorageReference resimYolu;

    String ilceId="";
    //Modelim
    Yerler yeniYer;

    //RecyclerView
    FirebaseRecyclerAdapter<Yerler, YerViewHolder> adapter;
    RecyclerView recycler_yer;

    RecyclerView.LayoutManager layoutManager; // arka plan yerleşimini ayarlamak için


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yerler);



        btn_yer_ekle=findViewById(R.id.btn_yer_ekle);

        //RecyclerView
        recycler_yer= findViewById(R.id.recyler_yerler);
        recycler_yer.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recycler_yer.setLayoutManager(layoutManager);

       /* yer_izleme_id=findViewById(R.id.txt_video_id);
        btn_izle=findViewById(R.id.yer_izleme_butonu);


        btn_izle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoId=new Intent(YerlerActivity.this,VideoIzlemeActivity.class);
                videoId.putExtra("Link",yer_izleme_id.getText().toString());
                startActivity(videoId);
            }
        });*/

        //Firebase tanımlamaları
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        yerYolu= database.getReference("Yerler");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        resimYolu= storage.getReference();



        if(getIntent()!=null)
        {
            ilceId=getIntent().getStringExtra("ilceId");
        }
        if(ilceId!=null)
            yerleriYukle(ilceId);




        btn_yer_ekle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                yerEklemePenceresiGoster();
            }


        });



    }

    private void yerleriYukle(String ilceId) {
        Query filtrele = yerYolu.orderByChild("ilceId").equalTo(ilceId);
        FirebaseRecyclerOptions<Yerler> secenekler=new FirebaseRecyclerOptions.Builder<Yerler>()
                .setQuery(filtrele,Yerler.class)
                .build();

        adapter=new FirebaseRecyclerAdapter<Yerler,YerViewHolder>(secenekler) {
            @Override
            protected void onBindViewHolder(@NonNull YerViewHolder holder, int position, @NonNull Yerler model) {
                holder.txtYerAdi.setText(model.getYerAdi());
                holder.txtTanıtıcıMetin.setText(model.getTanıtıcıMetin());
                holder.txtKonum.setText(model.getKonum());
                holder.btn_video.setText(model.getIzlemeLinki());
                Picasso.with(getBaseContext()).load(model.getResim()).into(holder.imageView);

                /*holder.setItemClickListener((view, position1, isLongClick) ->

                public void onClick(View view, int position, boolean isLongClick) {
                    Intent yerler=new Intent(IlcelerActivity.this,YerlerActivity.class);
                    yerler.putExtra("ilceId",adapter.getRef(position).getKey());
                    startActivity(yerler);

                });*/

            }
            @NonNull
            @Override
            public YerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.yer_satiri_ogesi,parent,false);
                return new YerViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged();
        recycler_yer.setAdapter(adapter);
    }

    private void yerEklemePenceresiGoster() {
        AlertDialog.Builder builder=new AlertDialog.Builder(YerlerActivity.this);
        builder.setTitle("Yeni Yer Ekle");
        builder.setMessage("Lütfen bilgilerinizi yazın");
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View yeni_yer_ekleme_penceresi=layoutInflater.inflate(R.layout.yeni_yer_ekleme_penceresi,null);

        edtYerAdi=yeni_yer_ekleme_penceresi.findViewById(R.id.edt_yer_adi);
        edtTanıtıcıMetin=yeni_yer_ekleme_penceresi.findViewById(R.id.edt_tanıtıcı_metin);
        edtKonum=yeni_yer_ekleme_penceresi.findViewById(R.id.edt_konum);
        edtIzlemeLinki=yeni_yer_ekleme_penceresi.findViewById(R.id.edt_izleme_linki);
        btnSec=yeni_yer_ekleme_penceresi.findViewById(R.id.btnSec);
        btnYukle=yeni_yer_ekleme_penceresi.findViewById(R.id.btnYukle);

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

        builder.setView(yeni_yer_ekleme_penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which){

                if(yeniYer!=null)
                {
                    yerYolu.push().setValue((yeniYer)); //veritabanı yolumu push ettim
                    Toast.makeText(YerlerActivity.this, yeniYer.getYerAdi()+"/t şehri eklendi", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(YerlerActivity.this, "Resim Yüklendi", Toast.LENGTH_SHORT).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //resmin veri tabanına aktarıldığı yer
                            yeniYer=new Yerler();
                            yeniYer.setYerAdi(edtYerAdi.getText().toString());
                            yeniYer.setTanıtıcıMetin(edtTanıtıcıMetin.getText().toString());
                            yeniYer.setKonum(edtKonum.getText().toString());
                            yeniYer.setIzlemeLinki(edtIzlemeLinki.getText().toString());
                            yeniYer.setIlceId(ilceId);
                            yeniYer.setResim(uri.toString());
                            //resmi şehirler modeline aktartıyoruz bu modeli de veritabanına göndermeliyiz builder.setpositivebuttonla
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(YerlerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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
            yerSil(adapter.getRef(item.getOrder()).getKey());
        }
        else if(item.getTitle().equals("Güncelle"))
        {
            yerGuncellemePenceresiGoster(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        return super.onContextItemSelected(item);
    }

    private void yerGuncellemePenceresiGoster(String key, Yerler item) {
        AlertDialog.Builder builder=new AlertDialog.Builder(YerlerActivity.this);
        builder.setTitle("Yeni Yer Ekle");
        builder.setMessage("Lütfen bilgilerinizi yazın");
        LayoutInflater layoutInflater=this.getLayoutInflater();
        View yeni_yer_ekleme_penceresi=layoutInflater.inflate(R.layout.yeni_yer_ekleme_penceresi,null);

        edtYerAdi=yeni_yer_ekleme_penceresi.findViewById(R.id.edt_yer_adi);
        edtTanıtıcıMetin=yeni_yer_ekleme_penceresi.findViewById(R.id.edt_tanıtıcı_metin);
        edtKonum=yeni_yer_ekleme_penceresi.findViewById(R.id.edt_konum);
        edtIzlemeLinki=yeni_yer_ekleme_penceresi.findViewById(R.id.edt_izleme_linki);
        btnSec=yeni_yer_ekleme_penceresi.findViewById(R.id.btnSec);
        btnYukle=yeni_yer_ekleme_penceresi.findViewById(R.id.btnYukle);

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

        builder.setView(yeni_yer_ekleme_penceresi);
        builder.setIcon(R.drawable.ic_action_name);

        builder.setPositiveButton("GÜNCELLE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which){
                item.setYerAdi(edtYerAdi.getText().toString());
                yerYolu.child(key).setValue(item);
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

    private void resimDegis(Yerler item) {
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
                    Toast.makeText(YerlerActivity.this, "Resim Güncellendi", Toast.LENGTH_SHORT).show();
                    resimDosyasi.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //resmin veri tabanına aktarıldığı yer

                            item.setResim(uri.toString());
                            yeniYer=new Yerler();
                            yeniYer.setYerAdi(edtYerAdi.getText().toString());
                            yeniYer.setTanıtıcıMetin(edtTanıtıcıMetin.getText().toString());
                            yeniYer.setKonum(edtKonum.getText().toString());
                            yeniYer.setIzlemeLinki(edtIzlemeLinki.getText().toString());
                            yeniYer.setIlceId(ilceId);
                            yeniYer.setResim(uri.toString());
                            //resmi şehirler modeline aktartıyoruz bu modeli de veritabanına göndermeliyiz builder.setpositivebuttonla
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(YerlerActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

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

    private void yerSil(String key) {
        yerYolu.child(key).removeValue();
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