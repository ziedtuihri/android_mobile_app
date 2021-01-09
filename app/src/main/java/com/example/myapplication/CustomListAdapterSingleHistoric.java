
package com.example.myapplication;
import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.TextView;

        import androidx.appcompat.app.AppCompatActivity;

        import java.util.List;


public class CustomListAdapterSingleHistoric extends BaseAdapter {
    private AppCompatActivity activity;
    private LayoutInflater inflater;
    private List<ListProductHistorique> historiques;

    public CustomListAdapterSingleHistoric(AppCompatActivity activity, List<ListProductHistorique> historiques) {
        this.activity = activity;
        this.historiques = historiques;
    }

    @Override
    public int getCount() {
        return historiques.size();
    }

    @Override
    public Object getItem(int location) {
        return historiques.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_items_single_historique, null);

        TextView date = (TextView) convertView.findViewById(R.id.date);
        TextView idHistorique = convertView.findViewById(R.id.idHistoricList) ;
        // getting historiques data for the row
        ListProductHistorique historique = historiques.get(position);
        idHistorique.setText(historique.getIdHistorique());
        date.setText(historique.getDate());

        return convertView;
    }

}