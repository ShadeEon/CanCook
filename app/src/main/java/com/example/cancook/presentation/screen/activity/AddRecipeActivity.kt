package com.example.cancook.presentation.screen.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.cancook.R
import com.example.cancook.data.local.RecipeDao
import com.example.cancook.data.local.RecipeEntity
import com.example.cancook.presentation.viewmodel.AddRecipeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddRecipeActivity : AppCompatActivity() {

    private val viewModel: AddRecipeViewModel by viewModel()
    private val recipeDao: RecipeDao by inject()

    private lateinit var mealTypeSpinner: Spinner
    private lateinit var difficultySpinner: Spinner
    private lateinit var tagSpinner: Spinner
    private lateinit var mealChipGroup: ChipGroup
    private lateinit var tagChipGroup: ChipGroup
    private lateinit var ingredientInputField: EditText
    private lateinit var instructionInputField: EditText
    private lateinit var ingredientsList: LinearLayout
    private lateinit var instructionsList: LinearLayout
    private lateinit var imageUploadBlock: ConstraintLayout
    private lateinit var uploadedImageView: ImageView
    private lateinit var uploadIcon: ImageView
    private lateinit var clearImageButton: ImageView
    private lateinit var saveButton: Button
    private lateinit var updateButton: ImageView
    private lateinit var closeButton: ImageView
    private lateinit var cancelButton: Button

    private lateinit var titleInputField: EditText
    private lateinit var cuisineEditText: EditText
    private lateinit var descriptionInputField: EditText
    private lateinit var ratingInputField: EditText
    private lateinit var prepTimeInputField: EditText
    private lateinit var cookTimeInputField: EditText
    private lateinit var servingInputField: EditText
    private lateinit var caloriesInputField: EditText

    private val ingredients = mutableListOf<String>()
    private val instructions = mutableListOf<String>()
    private var selectedMealTypes = mutableListOf<String>()
    private var selectedTags = mutableListOf<String>()
    private var selectedImageUri: String? = null
    private var currentRecipe: RecipeEntity? = null
    private var isViewingExisting = false

    private var currentPhotoUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                handleImageUri(uri)
            }
        }
    }

    // Camera Capture Launcher
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                handleImageUri(uri)
            }
        }
    }

    // Camera Permission Launcher
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                launchCameraIntent()
            } else {
                Toast.makeText(this, "Camera permission is required to take photos.", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        setupUI()
        observeViewModel()
        setupSpinnerListeners()
        setupInputListeners()

        currentRecipe = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("local_recipe", RecipeEntity::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("local_recipe")
        }

        if (currentRecipe != null) {
            isViewingExisting = true
            populateRecipeData(currentRecipe!!)
            updateButton.setImageResource(R.drawable.ic_delete)
            saveButton.text = "Update"
        }

        updateButton.setOnClickListener { showDeleteDialog() }
        saveButton.setOnClickListener { showPublishConfirmationDialog() }
    }

    private fun setupUI() {
        mealTypeSpinner = findViewById(R.id.mealTypeSpinner)
        difficultySpinner = findViewById(R.id.difficultySpinner)
        tagSpinner = findViewById(R.id.tagsSpinner)
        mealChipGroup = findViewById(R.id.mealTypeChipGroup)
        tagChipGroup = findViewById(R.id.tagsChipGroup)
        ingredientsList = findViewById(R.id.ingredientsList)
        instructionsList = findViewById(R.id.instructionsList)
        imageUploadBlock = findViewById(R.id.imageUploadBlock)
        uploadedImageView = findViewById(R.id.uploadedImageView)
        uploadIcon = findViewById(R.id.uploadIcon)
        clearImageButton = findViewById(R.id.clearImageButton)
        saveButton = findViewById(R.id.saveDraftButton)
        updateButton = findViewById(R.id.updateButton)
        closeButton = findViewById(R.id.closeButton)
        cancelButton = findViewById(R.id.cancelButton)

        ingredientInputField = findViewById(R.id.ingredientInputField)
        instructionInputField = findViewById(R.id.instructionInputField)
        titleInputField = findViewById(R.id.titleInputField)
        cuisineEditText = findViewById(R.id.cuisineEditText)
        descriptionInputField = findViewById(R.id.descriptionInputField)
        ratingInputField = findViewById(R.id.ratingInputField)
        prepTimeInputField = findViewById(R.id.prepTimeInputField)
        cookTimeInputField = findViewById(R.id.cookTimeInputField)
        servingInputField = findViewById(R.id.servingInputField)
        caloriesInputField = findViewById(R.id.caloriesInputField)

        imageUploadBlock.setOnClickListener {
            if (selectedImageUri == null) showImageSourceDialog()
            else Toast.makeText(this, "Clear image first to upload new one.", Toast.LENGTH_SHORT).show()
        }

        clearImageButton.setOnClickListener {
            selectedImageUri = null
            uploadedImageView.setImageDrawable(null)
            uploadedImageView.visibility = View.GONE
            uploadIcon.visibility = View.VISIBLE
            clearImageButton.visibility = View.GONE
        }

        closeButton.setOnClickListener { finish() }
        cancelButton.setOnClickListener { finish() }
    }

    private fun setupInputListeners() {
        // --- INGREDIENTS ---
        ingredientInputField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = ingredientInputField.text.toString().trim()
                if (text.isNotEmpty()) {
                    var isDuplicate = false
                    for (i in 0 until ingredientsList.childCount) {
                        val existingView = ingredientsList.getChildAt(i)
                        val existingEditText = existingView.findViewById<EditText>(R.id.ingredientItemField)
                        if (existingEditText?.text.toString().trim().equals(text, ignoreCase = true)) {
                            isDuplicate = true
                            break
                        }
                    }

                    if (!isDuplicate) {
                        val newView = LayoutInflater.from(this)
                            .inflate(R.layout.item_ingredient_input, ingredientsList, false)

                        val editText = newView.findViewById<EditText>(R.id.ingredientItemField)
                        editText.setText(text)

                        val clearButton = newView.findViewById<ImageView>(R.id.ingredient_clear_3)
                        clearButton.setOnClickListener {
                            ingredientsList.removeView(newView)
                        }

                        ingredientsList.addView(newView)
                    } else {
                        Toast.makeText(this, "Ingredient already added", Toast.LENGTH_SHORT).show()
                    }

                    ingredientInputField.text?.clear()
                }
                true
            } else false
        }

        // --- INSTRUCTIONS ---
        instructionInputField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val text = instructionInputField.text.toString().trim()
                if (text.isNotEmpty()) {
                    var isDuplicate = false
                    for (i in 0 until instructionsList.childCount) {
                        val existingView = instructionsList.getChildAt(i)
                        val existingEditText = existingView.findViewById<EditText>(R.id.instructionItemField)
                        if (existingEditText?.text.toString().trim().equals(text, ignoreCase = true)) {
                            isDuplicate = true
                            break
                        }
                    }

                    if (!isDuplicate) {
                        val newView = LayoutInflater.from(this)
                            .inflate(R.layout.item_instruction_input, instructionsList, false)

                        val editText = newView.findViewById<EditText>(R.id.instructionItemField)
                        editText.setText(text)

                        val clearButton = newView.findViewById<ImageView>(R.id.instruction_clear_3)
                        clearButton.setOnClickListener {
                            instructionsList.removeView(newView)
                        }

                        instructionsList.addView(newView)
                    } else {
                        Toast.makeText(this, "Instruction already added", Toast.LENGTH_SHORT).show()
                    }

                    instructionInputField.text?.clear()
                }
                true
            } else false
        }
    }

    private fun setupSpinnerListeners() {
        mealTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position) as String
                if (selected != "Select Meal Type" && !selectedMealTypes.contains(selected)) {
                    selectedMealTypes.add(selected)
                    addChip(selected, mealChipGroup) { type ->
                        selectedMealTypes.remove(type)
                        updateMealTypeSpinner(viewModel.mealTypes.value ?: emptyList())
                    }
                    updateMealTypeSpinner(viewModel.mealTypes.value ?: emptyList())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        tagSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position) as String
                if (selected != "Select Tag" && !selectedTags.contains(selected)) {
                    selectedTags.add(selected)
                    addChip(selected, tagChipGroup) { tag ->
                        selectedTags.remove(tag)
                        updateTagSpinner(viewModel.tags.value ?: emptyList())
                    }
                    updateTagSpinner(viewModel.tags.value ?: emptyList())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun addChip(text: String, chipGroup: ChipGroup, onRemove: (String) -> Unit) {
        val chip = Chip(this).apply {
            this.text = text
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                chipGroup.removeView(this)
                onRemove(text)
            }
        }
        chipGroup.addView(chip)
    }

    private fun observeViewModel() {
        viewModel.difficulties.observe(this) { updateSimpleSpinner(difficultySpinner, it) }
        viewModel.mealTypes.observe(this) { updateMealTypeSpinner(it) }
        viewModel.tags.observe(this) { updateTagSpinner(it) }
    }

    private fun updateSimpleSpinner(spinner: Spinner, items: List<String>) {
        val spinnerItems = mutableListOf("Select Difficulty")
        spinnerItems.addAll(items)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun updateMealTypeSpinner(allMealTypes: List<String>) {
        val available = listOf("Select Meal Type") + allMealTypes.filter { !selectedMealTypes.contains(it) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, available)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mealTypeSpinner.adapter = adapter
    }

    private fun updateTagSpinner(allTags: List<String>) {
        val available = listOf("Select Tag") + allTags.filter { !selectedTags.contains(it) }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, available)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tagSpinner.adapter = adapter
    }

    private fun addIngredientField(text: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_ingredient_input, ingredientsList, false)
        val editText = view.findViewById<EditText>(R.id.ingredientItemField)
        editText.setText(text)
        view.findViewById<ImageView>(R.id.ingredient_clear_3).setOnClickListener {
            ingredientsList.removeView(view)
        }
        ingredientsList.addView(view)
    }

    private fun addInstructionField(text: String) {
        val view = LayoutInflater.from(this).inflate(R.layout.item_instruction_input, instructionsList, false)
        val editText = view.findViewById<EditText>(R.id.instructionItemField)
        editText.setText(text)
        view.findViewById<ImageView>(R.id.instruction_clear_3).setOnClickListener {
            instructionsList.removeView(view)
        }
        instructionsList.addView(view)
    }

    private fun showImageSourceDialog() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder: android.app.AlertDialog.Builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Image Source")
        builder.setItems(options) { dialog, item ->
            when (options[item]) {
                "Take Photo" -> {
                    if (checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        launchCameraIntent()
                    } else {
                        requestCameraPermission.launch(android.Manifest.permission.CAMERA)
                    }
                }
                "Choose from Gallery" -> openGallery()
                "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        pickImage.launch(intent)
    }

    private fun launchCameraIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Toast.makeText(this, "Error creating image file.", Toast.LENGTH_SHORT).show()
            null
        }

        photoFile?.also {
            val photoUri: Uri = FileProvider.getUriForFile(
                this,
                "com.example.cancook.fileprovider",
                it
            )
            currentPhotoUri = photoUri
            takePicture.launch(photoUri)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    private fun handleImageUri(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

        selectedImageUri = uri.toString()
        uploadedImageView.setImageURI(uri)
        uploadedImageView.visibility = View.VISIBLE
        uploadIcon.visibility = View.GONE
        clearImageButton.visibility = View.VISIBLE
    }

    private fun saveRecipeToDatabase() {
        val name = titleInputField.text.toString().trim()
        val cuisine = cuisineEditText.text.toString().trim()
        val description = descriptionInputField.text.toString().trim()
        val rating = ratingInputField.text.toString().toFloatOrNull() ?: 0f
        val prepTime = prepTimeInputField.text.toString().toIntOrNull() ?: 0
        val cookTime = cookTimeInputField.text.toString().toIntOrNull() ?: 0
        val servings = servingInputField.text.toString().toIntOrNull() ?: 1
        val calories = caloriesInputField.text.toString().toIntOrNull() ?: 0
        val difficulty = difficultySpinner.selectedItem?.toString()

        if (name.isBlank()) {
            Toast.makeText(this, "Please enter recipe name", Toast.LENGTH_SHORT).show()
            return
        }

        val currentIngredients = mutableListOf<String>()
        for (i in 0 until ingredientsList.childCount) {
            val parentItemView = ingredientsList.getChildAt(i)
            val editText = parentItemView.findViewById<EditText>(R.id.ingredientItemField)
            val value = editText?.text?.toString()?.trim()
            if (!value.isNullOrEmpty()) {
                currentIngredients.add(value)
            }
        }

        val currentInstructions = mutableListOf<String>()
        for (i in 0 until instructionsList.childCount) {
            val parentItemView = instructionsList.getChildAt(i)
            val editText = parentItemView.findViewById<EditText>(R.id.instructionItemField)
            val value = editText?.text?.toString()?.trim()
            if (!value.isNullOrEmpty()) {
                currentInstructions.add(value)
            }
        }

        if (ingredientInputField.text.toString().trim().isNotEmpty()) {
            currentIngredients.add(ingredientInputField.text.toString().trim())
        }
        if (instructionInputField.text.toString().trim().isNotEmpty()) {
            currentInstructions.add(instructionInputField.text.toString().trim())
        }


        val recipe = RecipeEntity(
            localId = currentRecipe?.localId ?: 0L,
            name = name,
            description = description,
            // Use the collected lists
            ingredients = currentIngredients,
            instructions = currentInstructions,
            prepTimeMinutes = prepTime,
            cookTimeMinutes = cookTime,
            servings = servings,
            difficulty = difficulty,
            cuisine = cuisine,
            caloriesPerServing = calories,
            tags = selectedTags,
            imageUrl = selectedImageUri,
            rating = rating,
            mealType = selectedMealTypes,
            reviewCount = null,
            isFavorite = currentRecipe?.isFavorite ?: false
        )

        lifecycleScope.launch {
            if (isViewingExisting && currentRecipe != null) {
                recipeDao.updateRecipe(recipe.copy(localId = currentRecipe!!.localId))
                Toast.makeText(this@AddRecipeActivity, "Recipe updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                recipeDao.insertRecipe(recipe)
                Toast.makeText(this@AddRecipeActivity, "Recipe added successfully!", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun populateRecipeData(recipe: RecipeEntity) {
        titleInputField.setText(recipe.name)
        descriptionInputField.setText(recipe.description)
        cuisineEditText.setText(recipe.cuisine)
        ratingInputField.setText(recipe.rating?.toString() ?: "")
        prepTimeInputField.setText(recipe.prepTimeMinutes?.toString() ?: "")
        cookTimeInputField.setText(recipe.cookTimeMinutes?.toString() ?: "")
        servingInputField.setText(recipe.servings?.toString() ?: "")
        caloriesInputField.setText(recipe.caloriesPerServing?.toString() ?: "")

        selectedImageUri = recipe.imageUrl
        if (!selectedImageUri.isNullOrEmpty()) {
            uploadedImageView.visibility = View.VISIBLE
            uploadIcon.visibility = View.GONE
            Glide.with(this)
                .load(selectedImageUri)
                .placeholder(R.drawable.shimmer_placeholder)
                .error(R.drawable.drawable_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(uploadedImageView)
            clearImageButton.visibility = View.VISIBLE
        }

        recipe.ingredients.forEach { addIngredientField(it) }
        recipe.instructions.forEach { addInstructionField(it) }

        recipe.tags.forEach {
            selectedTags.add(it)
            addChip(it, tagChipGroup) { tag -> selectedTags.remove(tag); updateTagSpinner(viewModel.tags.value ?: emptyList()) }
        }
        recipe.mealType.forEach {
            selectedMealTypes.add(it)
            addChip(it, mealChipGroup) { type -> selectedMealTypes.remove(type); updateMealTypeSpinner(viewModel.mealTypes.value ?: emptyList()) }
        }
        updateTagSpinner(viewModel.tags.value ?: emptyList())
        updateMealTypeSpinner(viewModel.mealTypes.value ?: emptyList())
    }

    private fun showPublishConfirmationDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Save Recipe")
            .setMessage("Do you want to save this recipe locally?")
            .setPositiveButton("Yes") { d, _ ->
                saveRecipeToDatabase()
                d.dismiss()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showDeleteDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Delete Recipe")
            .setMessage("Are you sure you want to delete this recipe?")
            .setPositiveButton("Delete") { _, _ ->
                currentRecipe?.let {
                    lifecycleScope.launch {
                        recipeDao.deleteRecipe(it)
                        Toast.makeText(this@AddRecipeActivity, "Recipe deleted!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}