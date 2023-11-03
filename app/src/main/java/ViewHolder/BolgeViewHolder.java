package ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seyahat_rehber.R;

import Interface.ItemClickListener;

public class BolgeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener {

    public TextView txtBolgeAdi;
    public ImageView imageView;

    private ItemClickListener itemClickListener; //arayüzüm

    public BolgeViewHolder(@NonNull View itemView) {
        super(itemView);
        txtBolgeAdi=itemView.findViewById(R.id.bolge_adii);
        imageView=itemView.findViewById(R.id.bolge_resmi);
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
