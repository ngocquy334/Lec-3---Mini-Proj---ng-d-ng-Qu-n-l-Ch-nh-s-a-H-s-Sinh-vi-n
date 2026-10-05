package com.example.studentprofilemanager

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.studentprofilemanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val tag = "MainActivityLifecycle"
    private lateinit var binding: ActivityMainBinding
    private var student = Student("2415053122235", "Trương Ngọc Quý", "24T2", "tt598958@gmail.com", 3.8)

    // 1. Launcher nhận kết quả từ EditProfileActivity
    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        if (res.resultCode == RESULT_OK) {
            val updatedStudent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                res.data?.getSerializableExtra("UPDATED", Student::class.java)
            } else {
                @Suppress("DEPRECATION")
                res.data?.getSerializableExtra("UPDATED") as? Student
            }

            updatedStudent?.let {
                student = it
                bindData(student)
                Toast.makeText(this, "Đã lưu thành công!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. Launcher chọn ảnh từ thư viện
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.imgAvatar.setImageURI(it)
            Toast.makeText(this, "Đã đổi avatar!", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Launcher yêu cầu cấp quyền Camera
    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { ok ->
        if (ok) {
            Toast.makeText(this, "Đã cấp quyền Camera!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Bị từ chối quyền!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(tag, "onCreate called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindData(student)

        // Nút Sửa: Gửi dữ liệu qua EditProfileActivity
        binding.btnEditProfile.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java).apply {
                putExtra("STUDENT", student)
            }
            editLauncher.launch(intent)
        }

        // Nút Đổi ảnh: Mở picker hình ảnh
        binding.btnChangeAvatar.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Nút Gọi điện: Implicit Intent ACTION_DIAL
        binding.btnCallHotline.setOnClickListener {
            val dialIntent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:0123456789")
            }
            startActivity(dialIntent)
        }

        // Nút Xin quyền Camera
        binding.btnRequestCamera.setOnClickListener {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun bindData(student: Student) {
        binding.tvName.text = student.name
        binding.tvDetails.text = "MSSV: ${student.id} | Lớp: ${student.className}"

        val rank = when {
            student.gpa >= 3.6 -> "Xuất sắc"
            student.gpa >= 3.2 -> "Giỏi"
            student.gpa >= 2.5 -> "Khá"
            else -> "Trung bình"
        }
        binding.tvGpaBadge.text = "GPA: ${student.gpa} ($rank)"
    }

    override fun onStart() {
        super.onStart()
        Log.d(tag, "onStart called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(tag, "onResume called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(tag, "onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(tag, "onStop called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(tag, "onDestroy called")
    }
}