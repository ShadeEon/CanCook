package com.example.cancook.presentation.screen.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.cancook.R
import com.example.cancook.data.local.RecipeDao
import com.example.cancook.data.local.RecipeEntity
import com.example.cancook.presentation.viewmodel.AddRecipeViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddRecipeActivity : AppCompatActivity() {

    private val viewModel: AddRecipeViewModel by viewModel()

    private lateinit var mealTypeSpinner: Spinner
    private lateinit var difficultySpinner: Spinner
    private lateinit var tagSpinner: Spinner
    private lateinit var mealChipGroup: ChipGroup
    private lateinit var tagChipGroup: ChipGroup

    // Dynamic ingredient and instruction containers
    private lateinit var ingredientsList: LinearLayout
    private lateinit var instructionsList: LinearLayout

    private val ingredients = mutableListOf<String>()
    private val instructions = mutableListOf<String>()

    private var selectedMealTypes = mutableListOf<String>()
    private var selectedTags = mutableListOf<String>()
    private var selectedDifficulty: String? = null
    private val recipeDao: RecipeDao by inject()

    // Image Upload related views
    private lateinit var imageUploadBlock: ConstraintLayout // Reference to the clickable area
    private lateinit var uploadedImageView: ImageView        // View to display the selected image
    private lateinit var uploadIcon: ImageView               // The initial icon

    // Variable to hold the URI of the selected image
    private var selectedImageUri: String? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = result.data?.data
            if (uri != null) {
                selectedImageUri = uri.toString()
                uploadedImageView.setImageURI(uri)
                uploadedImageView.visibility = View.VISIBLE
                uploadIcon.visibility = View.GONE
                clearImageButton.visibility = View.VISIBLE
            }
        }
    }

    private lateinit var clearImageButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        // Initialize spinners and chip groups
        mealTypeSpinner = findViewById(R.id.mealTypeSpinner)
        difficultySpinner = findViewById(R.id.difficultySpinner)
        tagSpinner = findViewById(R.id.tagsSpinner)
        mealChipGroup = findViewById(R.id.mealTypeChipGroup)
        tagChipGroup = findViewById(R.id.tagsChipGroup)

        // Initialize dynamic input sections
        ingredientsList = findViewById(R.id.ingredientsList)
        instructionsList = findViewById(R.id.instructionsList)

        clearImageButton = findViewById(R.id.clearImageButton)
        imageUploadBlock = findViewById(R.id.imageUploadBlock)
        uploadedImageView = findViewById(R.id.uploadedImageView)
        uploadIcon = findViewById(R.id.uploadIcon)

        // Initialize main input fields
        val ingredientInputField = findViewById<EditText>(R.id.ingredientInputField)
        val instructionInputField = findViewById<EditText>(R.id.instructionInputField)

        observeViewModel()
        setupSpinners()

        val publishButton: View = findViewById(R.id.saveDraftButton)
        publishButton.setOnClickListener {
            showPublishConfirmationDialog()
        }

        imageUploadBlock.setOnClickListener {
            if (selectedImageUri == null) {
                openImageChooser()
            }
            else {
                Toast.makeText(this, "Image already uploaded. Clear it first to upload a new one.", Toast.LENGTH_SHORT).show()
            }
        }

        clearImageButton.setOnClickListener {
            selectedImageUri = null
            uploadedImageView.setImageDrawable(null)
            uploadedImageView.visibility = View.GONE
            uploadIcon.visibility = View.VISIBLE
            clearImageButton.visibility = View.GONE
        }

        // Editor action listeners
        ingredientInputField.setOnEditorActionListener { _, _, _ ->
            val text = ingredientInputField.text.toString().trim()
            if (text.isNotEmpty()) {
                addIngredient(text)
            }
            true
        }

        instructionInputField.setOnEditorActionListener { _, _, _ ->
            val text = instructionInputField.text.toString().trim()
            if (text.isNotEmpty()) {
                addInstruction(text)
            }
            true
        }
    }

    private fun showPublishConfirmationDialog() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Publish Recipe")
            .setMessage("Are you sure you want to publish this recipe?")
            .setPositiveButton("Yes") { dialogInterface, _ ->
                saveRecipeToDatabase()
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            // Flags needed for persistent URI access in Room/local storage
            flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        pickImage.launch(intent)
    }

    private fun saveRecipeToDatabase() {
        val titleInput = findViewById<EditText>(R.id.titleInputField)
        val cuisineInput = findViewById<EditText>(R.id.cuisineEditText)
        val ratingInput = findViewById<EditText>(R.id.ratingInputField)
        val prepTimeInput = findViewById<EditText>(R.id.prepTimeInputField)
        val cookTimeInput = findViewById<EditText>(R.id.cookTimeInputField)
        val servingInput = findViewById<EditText>(R.id.servingInputField)
        val caloriesInput = findViewById<EditText>(R.id.caloriesInputField)
        val descriptionInput = findViewById<EditText>(R.id.descriptionInputField)

        ingredients.clear()
        for (i in 0 until ingredientsList.childCount) {
            val view = ingredientsList.getChildAt(i)
            val input = when (view) {
                is EditText -> view
                else -> view.findViewById<EditText>(R.id.ingredientItemField)
            }
            val text = input?.text.toString().trim()
            if (text.isNotEmpty()) ingredients.add(text)
        }

        for (i in 0 until instructionsList.childCount) {
            val view = instructionsList.getChildAt(i)
            val input = when (view) {
                is EditText -> view
                else -> view.findViewById<EditText>(R.id.instructionItemField)
            }
            val text = input?.text.toString().trim()
            if (text.isNotEmpty()) instructions.add(text)
        }

        val cuisine = cuisineInput.text.toString().trim()
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val rating = ratingInput.text.toString().toFloatOrNull() ?: 0f
        val prepTime = prepTimeInput.text.toString().toIntOrNull() ?: 0
        val cookTime = cookTimeInput.text.toString().toIntOrNull() ?: 0
        val servings = servingInput.text.toString().toIntOrNull() ?: 1
        val calories = caloriesInput.text.toString().toIntOrNull() ?: 0
        val selectedDifficulty = difficultySpinner.selectedItem?.toString()?.trim()



        if (title.isBlank()) {
            Toast.makeText(this, "Please enter recipe name", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedDifficulty.isNullOrEmpty() || selectedDifficulty.equals("Select Difficulty", true)) {
            Toast.makeText(this, "Please select a difficulty", Toast.LENGTH_SHORT).show()
            return
        }

        val recipe = RecipeEntity(
            name = title,
            description = description,
            ingredients = ingredients,
            instructions = instructions,
            prepTimeMinutes = prepTime,
            cookTimeMinutes = cookTime,
            servings = servings,
            difficulty = selectedDifficulty,
            cuisine = cuisine,
            caloriesPerServing = calories,
            tags = selectedTags,
            imageUrl = selectedImageUri,
            rating = rating,
            reviewCount = null,
            mealType = selectedMealTypes,
            isFavorite = false
        )

        lifecycleScope.launch {
            recipeDao.insertRecipe(recipe)
            Toast.makeText(this@AddRecipeActivity, "Recipe published successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.mealTypes.observe(this, Observer { mealTypes ->
            updateMealTypeSpinner(mealTypes)
        })
        viewModel.tags.observe(this, Observer { tags ->
            updateTagSpinner(tags)
        })
        viewModel.difficulties.observe(this, Observer { difficulties ->
            updateDifficultySpinner(difficulties)
        })
    }

    private fun setupSpinners() {
        mealTypeSpinner.setOnItemSelectedListener { _, _, _, _ ->
            val mealType = mealTypeSpinner.selectedItem as String
            if (mealType != "Select Meal Type" && !selectedMealTypes.contains(mealType)) {
                addChip(mealType, mealChipGroup) { chipText ->
                    selectedMealTypes.remove(chipText)
                    updateMealTypeSpinner(viewModel.mealTypes.value ?: emptyList())
                }
                selectedMealTypes.add(mealType)
                updateMealTypeSpinner(viewModel.mealTypes.value ?: emptyList())
            }
        }

        tagSpinner.setOnItemSelectedListener { _, _, _, _ ->
            val tag = tagSpinner.selectedItem as String
            if (tag != "Select Tag" && !selectedTags.contains(tag)) {
                addChip(tag, tagChipGroup) { chipText ->
                    selectedTags.remove(chipText)
                    updateTagSpinner(viewModel.tags.value ?: emptyList())
                }
                selectedTags.add(tag)
                updateTagSpinner(viewModel.tags.value ?: emptyList())
            }
        }

        difficultySpinner.setOnItemSelectedListener { _, _, _, _ ->
            selectedDifficulty = difficultySpinner.selectedItem as String
        }
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

    private fun updateDifficultySpinner(difficulties: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, difficulties)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        difficultySpinner.adapter = adapter
    }

    private fun addChip(text: String, chipGroup: ChipGroup, onRemove: (String) -> Unit) {
        val chip = Chip(this)
        chip.text = text
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip)
            onRemove(text)
        }
        chipGroup.addView(chip)
    }

    private fun Spinner.setOnItemSelectedListener(onItemSelected: (parent: Spinner, view: View?, position: Int, id: Long) -> Unit) {
        this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) =
                onItemSelected(parent as Spinner, view, position, id)
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun addIngredient(text: String = "") {
        val inflater = LayoutInflater.from(this)
        val ingredientView = inflater.inflate(R.layout.item_ingredient_input, ingredientsList, false)
        val editText = ingredientView.findViewById<EditText>(R.id.ingredientItemField)
        val clearButton = ingredientView.findViewById<ImageView>(R.id.ingredient_clear_3)

        editText.setText(text)
        clearButton.setOnClickListener { ingredientsList.removeView(ingredientView) }

        ingredientsList.addView(ingredientView)

        if (text.isNotEmpty()) {
            findViewById<EditText>(R.id.ingredientInputField).text.clear()
        }
    }

    private fun addInstruction(text: String = "") {
        val inflater = LayoutInflater.from(this)
        val instructionView = inflater.inflate(R.layout.item_instruction_input, instructionsList, false)
        val editText = instructionView.findViewById<EditText>(R.id.instructionItemField)
        val clearButton = instructionView.findViewById<ImageView>(R.id.instruction_clear_3)

        editText.setText(text)
        clearButton.setOnClickListener { instructionsList.removeView(instructionView) }

        instructionsList.addView(instructionView)

        if (text.isNotEmpty()) {
            findViewById<EditText>(R.id.instructionInputField).text.clear()
        }
    }
}
