// package com.example.ezyshop.service;

// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.Optional;
// import java.util.stream.Collectors;

// public class CategoryService {

//     private final CategoryRepository categoryRepository;

//     /**
//      * Membuat child category berdasarkan parent yang sudah tersedia (systemDefined).
//      */
//     @Transactional
//     public CategoryResponseDto createChildCategory(CategoryRequestDto dto, User seller) {
//         // Validasi parent category
//         Category parent = categoryRepository.findById(dto.getParentId())
//                 .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));

//         if (!parent.isSystemDefined()) {
//             throw new IllegalArgumentException("Only system-defined categories can have children.");
//         }

//         // Cek slug agar tidak duplikat
//         Optional<Category> existing = categoryRepository.findBySlug(dto.getSlug());
//         if (existing.isPresent()) {
//             throw new IllegalArgumentException("Category slug already exists.");
//         }

//         // Simpan kategori baru
//         Category child = Category.builder()
//                 .name(dto.getName())
//                 .slug(dto.getSlug())
//                 .parent(parent)
//                 .createdBy(seller)
//                 .systemDefined(false)
//                 .build();

//         Category saved = categoryRepository.save(child);

//         return mapToDto(saved);
//     }

//     /**
//      * Mengambil semua child dari parent tertentu.
//      */
//     @Transactional(readOnly = true)
//     public List<CategoryResponseDto> getChildrenOf(Long parentId) {
//         List<Category> children = categoryRepository.findByParentId(parentId);
//         return children.stream()
//                 .map(this::mapToDto)
//                 .collect(Collectors.toList());
//     }

//     /**
//      * Mendapatkan semua parent (system-defined) category.
//      */
//     @Transactional(readOnly = true)
//     public List<CategoryResponseDto> getAllParentCategories() {
//         return categoryRepository.findByParentIsNullAndSystemDefinedTrue()
//                 .stream()
//                 .map(this::mapToDto)
//                 .collect(Collectors.toList());
//     }

//     private CategoryResponseDto mapToDto(Category category) {
//         return CategoryResponseDto.builder()
//                 .id(category.getId())
//                 .name(category.getName())
//                 .slug(category.getSlug())
//                 .parentId(category.getParent() != null ? category.getParent().getId() : null)
//                 .build();
//     }
// }
