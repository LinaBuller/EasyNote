package com.easynote.domain.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.easynote.domain.models.Category
import com.easynote.domain.usecase.categories.DeleteCategoryUseCase
import com.easynote.domain.usecase.categories.GetCategoriesUseCase
import com.easynote.domain.usecase.categories.SetCategoriesUseCase
import com.easynote.domain.usecase.categories.UpdateCategoryUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoriesFragmentViewModel(
    val getCategoriesUseCase: GetCategoriesUseCase,
    val setCategoriesUseCase: SetCategoriesUseCase,
    val updateCategoryUseCase: UpdateCategoryUseCase,
    val deleteCategoryUseCase: DeleteCategoryUseCase,
) : BaseViewModel() {

    private val _categories = getCategoriesUseCase.execute()
    val categories: LiveData<List<Category>> get() = _categories

    fun setCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            setCategoriesUseCase.execute(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            updateCategoryUseCase.execute(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteCategoryUseCase.execute(category)
        }
    }

    private var selectedCategory = MutableLiveData<Category?>()


    fun setSelectedCategory(category: Category) {
        selectedCategory.value = category
    }

    fun clearSelectedCategory() {
        selectedCategory.value = null
    }

    fun updateSelectedCategory(title: String){
        if (selectedCategory.value!=null){
            updateCategory(selectedCategory.value!!.copy(titleCategory = title))
        }
        clearSelectedCategory()
    }

    fun deleteSelectedCategory(){
        if (selectedCategory.value!=null){
            deleteCategory(selectedCategory.value!!)
        }
        clearSelectedCategory()
    }

}