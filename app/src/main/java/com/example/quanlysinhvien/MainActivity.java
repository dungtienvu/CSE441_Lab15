package com.example.quanlysinhvien;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText edtmalop, edttenlop, edtsiso;
    Button btninsert, btndelete, btnupdate, btnquery;
    ListView lv;

    ArrayList<String> mylist;
    ArrayAdapter<String> myadapter;
    SQLiteDatabase mydatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtmalop = findViewById(R.id.edtMalop);
        edttenlop = findViewById(R.id.edtTenlop);
        edtsiso = findViewById(R.id.edtSiso);

        btninsert = findViewById(R.id.btnInsert);
        btndelete = findViewById(R.id.btnDelete);
        btnupdate = findViewById(R.id.btnUpdate);
        btnquery = findViewById(R.id.btnQuery);

        lv = findViewById(R.id.lvLop);

        mylist = new ArrayList<>();
        myadapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mylist);
        lv.setAdapter(myadapter);

        // Tạo và mở database
        mydatabase = openOrCreateDatabase("qlsinhvien.db", MODE_PRIVATE, null);

        // Tạo bảng nếu chưa tồn tại
        try {
            String sql = "CREATE TABLE IF NOT EXISTS tbllop (malop TEXT PRIMARY KEY, tenlop TEXT, siso INTEGER)";
            mydatabase.execSQL(sql);
        } catch (Exception e) {
            Log.e("Error", "Table đã tồn tại");
        }

        // Xử lý nút INSERT
        btninsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String malop = edtmalop.getText().toString();
                String tenlop = edttenlop.getText().toString();
                String sisoText = edtsiso.getText().toString();

                if (malop.isEmpty() || tenlop.isEmpty() || sisoText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                int siso = Integer.parseInt(sisoText);
                ContentValues myvalue = new ContentValues();
                myvalue.put("malop", malop);
                myvalue.put("tenlop", tenlop);
                myvalue.put("siso", siso);

                String msg = "";
                if (mydatabase.insert("tbllop", null, myvalue) == -1) {
                    msg = "Thêm thất bại (trùng mã lớp)";
                } else {
                    msg = "Thêm thành công";
                }
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút DELETE
        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String malop = edtmalop.getText().toString();
                int n = mydatabase.delete("tbllop", "malop = ?", new String[]{malop});
                String msg = (n == 0) ? "Không tìm thấy lớp để xóa" : n + " lớp đã bị xóa";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút UPDATE
        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String malop = edtmalop.getText().toString();
                String sisoText = edtsiso.getText().toString();

                if (malop.isEmpty() || sisoText.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập mã lớp và sĩ số mới", Toast.LENGTH_SHORT).show();
                    return;
                }

                int siso = Integer.parseInt(sisoText);
                ContentValues myvalue = new ContentValues();
                myvalue.put("siso", siso);

                int n = mydatabase.update("tbllop", myvalue, "malop = ?", new String[]{malop});
                String msg = (n == 0) ? "Không tìm thấy lớp để cập nhật" : n + " lớp đã được cập nhật";
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý nút QUERY
        btnquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mylist.clear();
                Cursor c = mydatabase.query("tbllop", null, null, null, null, null, null);
                while (c.moveToNext()) {
                    String data = c.getString(0) + " - " + c.getString(1) + " - " + c.getInt(2);
                    mylist.add(data);
                }
                c.close();
                myadapter.notifyDataSetChanged();
            }
        });
    }
}
