package com.tinkoff.androidcourse;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Worker> workerList;
    private RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        workerList = getWorkers();
        recycler = findViewById(R.id.recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new MyAdapter(workerList));
        recycler.addItemDecoration(
                new MyItemDecorator(getResources().getDrawable(R.drawable.divider)));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                workerList.remove(position);
                recycler.getAdapter().notifyItemRemoved(position);
            }
        });

        itemTouchHelper.attachToRecyclerView(recycler);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Worker worker = WorkerGenerator.generateWorker();
                workerList.add(worker);
                if (recycler != null) {
                    recycler.getAdapter().notifyItemRangeChanged(
                            workerList.size() - 1,
                            1
                    );
                }
            }
        });
    }

    private List<Worker> getWorkers() {
        Object workers = getLastCustomNonConfigurationInstance();
        if (workers instanceof List) {
            return (List<Worker>) workers;
        }

        return WorkerGenerator.generateWorkers(3);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return workerList;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final TextView ageTextView;
        private final TextView positionTextView;
        private final AppCompatImageView photoImageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            ageTextView = itemView.findViewById(R.id.age_text_view);
            positionTextView = itemView.findViewById(R.id.position_text_view);
            photoImageView = itemView.findViewById(R.id.photo_image_view);
        }

        public void bindWorker(Worker worker) {
            nameTextView.setText(worker.getName());
            ageTextView.setText(worker.getAge());
            positionTextView.setText(worker.getPosition());
            photoImageView.setImageResource(worker.getPhoto());
        }
    }

    private static class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private final List<Worker> workerList;

        public MyAdapter(List<Worker> workerList) {
            this.workerList = workerList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_layout, viewGroup, false);

            return new MyViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
            myViewHolder.bindWorker(workerList.get(i));
        }

        @Override
        public int getItemCount() {
            return workerList.size();
        }
    }

    private static class MyItemDecorator extends RecyclerView.ItemDecoration {
        private final Drawable drawable;

        public MyItemDecorator(Drawable drawable) {
            this.drawable = drawable;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect,
                                   @NonNull View view,
                                   @NonNull RecyclerView parent,
                                   @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            if (parent.getChildAdapterPosition(view) == 0) {
                return;
            }

            outRect.top = drawable.getIntrinsicHeight();
        }

        @Override
        public void onDraw(@NonNull Canvas canvas,
                           @NonNull RecyclerView parent,
                           @NonNull RecyclerView.State state) {
            int dividerLeft = parent.getPaddingLeft();
            int dividerRight = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount - 1; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + drawable.getIntrinsicHeight();

                drawable.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                drawable.draw(canvas);
            }
        }
    }
}
