package ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seyahat_rehber.R;

import Interface.ItemClickListener;

public class YerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView txtYerAdi;
    public TextView txtTanıtıcıMetin;
    public TextView txtKonum;
    public ImageView imageView;
    public Button btn_video;

    private ItemClickListener itemClickListener; //arayüzüm

    public YerViewHolder(@NonNull View itemView) {
        super(itemView);
        txtYerAdi=itemView.findViewById(R.id.yer_adii);
        txtTanıtıcıMetin=itemView.findViewById(R.id.yer_tanıtıcı_metin);
        txtKonum=itemView.findViewById(R.id.yer_konum);
        btn_video=itemView.findViewById(R.id.yer_izleme_butonu);
        imageView=itemView.findViewById(R.id.yer_resmi);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener=itemClickListener;
    }

    @Override
    public void onClick(View view) {

        itemClickListener.onClick(view,getAdapterPosition(),false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Eylem seçin");
        contextMenu.add(0,0,getAdapterPosition(),"Güncelle");
        contextMenu.add(0,1,getAdapterPosition(),"Sil");
    }
}

