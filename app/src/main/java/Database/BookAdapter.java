package Database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tall_tale.R;


import java.util.List;

import Activities.MainActivity;

//adapter for book card views on home page
public class BookAdapter  extends RecyclerView.Adapter<BookVH> {
    List<Integer> id;
    List<String> title;
    List<String> author;
    List<String> startDateTime;
    List<String> endDateTime;

    public BookAdapter(List<Integer> id, List<String> title, List<String> author, List<String> startDateTime, List<String> endDateTime) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }


    @NonNull
    @Override
    public BookVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_card_view, parent, false);

        return new BookVH(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull BookVH holder, int position) {
        holder.bookID.setText(id.get(position).toString());
        holder.bookTitle.setText(title.get(position).toString());
        holder.bookAuthor.setText(author.get(position).toString());
        holder.bookStartDateTime.setText(startDateTime.get(position).toString());
        holder.bookEndDateTime.setText(endDateTime.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return id.size();
    }
}

class BookVH extends RecyclerView.ViewHolder {
    private BookAdapter adapter;
    TextView bookID;
    TextView bookTitle;
    TextView bookAuthor;
    TextView bookStartDateTime;
    TextView bookEndDateTime;


    public BookVH(@NonNull View itemView) {
        super(itemView);

        bookID = itemView.findViewById(R.id.book_id);
        bookTitle = itemView.findViewById(R.id.book_title);
        bookAuthor = itemView.findViewById(R.id.book_author);
        bookStartDateTime = itemView.findViewById(R.id.book_start);
        bookEndDateTime = itemView.findViewById(R.id.book_end);

        //add/remove items from list of user selected items
        itemView.findViewById(R.id.bt_checkbox).setOnClickListener(view -> {
            //instantiate ImageButton object to change icon
            ImageButton checkbox = itemView.findViewById(R.id.bt_checkbox);
            //check if item is selected already
            if(MainActivity.selected != null) {
                for (int i = 0; i < MainActivity.selected.size(); i++) {
                    //if item is selected already
                    if (MainActivity.selected.get(i) == adapter.id.get(getAdapterPosition())) {
                        //uncheck box icon
                        checkbox.setImageResource(R.drawable.ic_unchecked_icon);
                        //remove item from selected list
                        MainActivity.selected.remove(i);
                        return;
                    }
                }
            }
            //if item is being selected
            checkbox.setImageResource(R.drawable.ic_selected_icon);
            MainActivity.selected.add(adapter.id.get(getAdapterPosition()));
        });



    }


    public BookVH linkAdapter(BookAdapter adapter) {
        this.adapter = adapter;
        return this;
    }


}













