package com.example.studentprofilemanager

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.studentprofilemanager.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    private val tag = "EditProfileLifecycle"
    private lateinit var binding: ActivityEditProfileBinding
    private var originalStudent: Student? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate called")
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận dữ liệu sinh viên cũ
        originalStudent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("STUDENT", Student::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("STUDENT") as? Student
        }

        // Tự động điền dữ liệu lên EditText
        originalStudent?.let {
            binding.edtName.setText(it.name)
            binding.edtClass.setText(it.className)
            binding.edtGpa.setText(it.gpa.toString())
        }

        // Nút Lưu và phản hồi
        binding.btnSave.setOnClickListener {
            val name = binding.edtName.text.toString().trim()
            val className = binding.edtClass.text.toString().trim()
            val gpa = binding.edtGpa.text.toString().toDoubleOrNull()

            // Kiểm tra tính hợp lệ (Validation)
            if (name.isEmpty() || className.isEmpty() || gpa == null || gpa !in 0.0..4.0) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ tên, lớp và GPA hợp lệ (0.0 - 4.0)!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = originalStudent?.copy(
                name = name,
                className = className,
                gpa = gpa
            ) ?: return@setOnClickListener

            val resIntent = Intent().apply {
                putExtra("UPDATED", updated)
            }
            setResult(RESULT_OK, resIntent)
            finish()
        }

        // Nút Hủy: Đóng Activity mà không gửi kết quả (mặc định RESULT_CANCELED)
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy called")
    }
}