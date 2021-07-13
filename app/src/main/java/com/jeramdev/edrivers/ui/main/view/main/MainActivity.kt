package com.jeramdev.edrivers.ui.main.view.main

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jeramdev.edrivers.R
import com.jeramdev.edrivers.databinding.ActivityMainBinding
import com.jeramdev.edrivers.utils.Constants
import com.jeramdev.edrivers.utils.UserPreferencesHelper

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val rol = UserPreferencesHelper(this).getRolPref()
        if (rol == Constants.DRIVING_INSTRUCTOR_ROL) {
            binding.bottomNavigationMenu.menu.removeItem(R.id.achievementsFragment);
        }

        val homeFragment = HomeFragment()
        val lessonsFragment = LessonsFragment()
        val achievementsFragment = AchievementsFragment()
        val profileFragment = ProfileFragment()

        // Inicializa la activity con el fragment de la pantalla de Inicio
        makeCurrentFragment(homeFragment)

        binding.bottomNavigationMenu.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> makeCurrentFragment(homeFragment)
                R.id.lessonsFragment -> makeCurrentFragment(lessonsFragment)
                R.id.achievementsFragment -> makeCurrentFragment(achievementsFragment)
                R.id.profileFragment -> makeCurrentFragment(profileFragment)
            }
            true
        }
    }

    /**
     * Muestra el fragment
     * @param fragment Fragment que se muestra
     */
    fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(binding.frameContainer.id, fragment)
            commit()
        }

    /**
     * Quita la selección de los elementos del menú
     */
    fun menuUnselectAll() {
        val menu = binding.bottomNavigationMenu.menu
        menu.setGroupCheckable(0, true, false)
        for (i in 0 until menu.size()) {
            menu.getItem(i).isChecked = false
        }
        menu.setGroupCheckable(0, true, true)
    }

    /**
     * Activa el menu
     */
    fun menuEnable() {
        binding.bottomNavigationMenu.menu.setGroupVisible(0, true)
        menuUnselectAll()
    }

    /**
     * Desactiva el menu
     */
    fun menuDisable() {
        binding.bottomNavigationMenu.menu.setGroupVisible(0, false)
    }

    /**
     * Evita el uso del botón atrás
     */
    override fun onBackPressed() {
        // do nothing.
    }
}