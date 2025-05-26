document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("searchInput");
    const foodContainer = document.getElementById("foodContainer");
    const pagination = document.getElementById("pagination");

    searchInput.addEventListener("input", function () {
        const query = this.value.trim();

        if (query.length > 0) {
            fetch(`/api/food/search?name=${query}`)
                .then(response => response.json())
                .then(data => {
                    renderSearchResults(data);
                    pagination.style.display = "none"; // hide pagination during search
                })
                .catch(error => console.error("Search error:", error));
        } else {
            // Restore paginated results
            if (typeof fetchAndRenderPage === "function") {
                fetchAndRenderPage(0);
                pagination.style.display = "flex"; // show pagination when search cleared
            }
        }
    });

    function renderSearchResults(foodList) {
        foodContainer.innerHTML = "";

        if (foodList.length === 0) {
            foodContainer.innerHTML = `<p class='text-center'>No matching food items found.</p>`;
            return;
        }

        foodList.forEach(food => {
            const col = document.createElement("div");
            col.className = "col-md-4 mb-4";
            col.innerHTML = `
                <div class="card h-100 shadow-sm border-0 rounded-4">
                    <img src="${food.imageUrl || '/images/default-food.jpg'}"
                         class="card-img-top rounded-top img-fluid"
                         alt="Food Image"
                         style="height: 200px; width: 100%; object-fit: cover;">
                    <div class="card-body d-flex flex-column">
                        <h5 class="card-title fw-bold">${food.name}</h5>
                        <span class="badge mb-2 ${food.category === 'Vegetarian' ? 'bg-success' : 'bg-danger'}">
                            ${food.category}
                        </span>
                        <p class="card-text text-muted small">${food.description}</p>
                        <h6 class="text-primary mb-3 fw-semibold">₹${food.price}</h6>
                        <div class="d-flex align-items-center justify-content-center gap-2 mt-2">
                            <button class="btn btn-outline-secondary btn-sm decrease-btn" data-id="${food.id}">−</button>
                            <input type="number" class="form-control text-center" value="1" min="1" data-id="${food.id}" style="width: 60px;" readonly>
                            <button class="btn btn-outline-secondary btn-sm increase-btn" data-id="${food.id}">+</button>
                        </div>
                        <div class="d-flex justify-content-between align-items-center gap-3 mt-3">
                            <button class="btn btn-warning flex-button fw-semibold rounded-3 shadow-sm addToCartBtn"
                                    data-id="${food.id}">Add to Cart</button>
                            <a href="/cart" class="btn btn-primary flex-button fw-semibold rounded-3 shadow-sm"
                               data-id="${food.id}">Order Now</a>
                        </div>
                    </div>
                </div>
            `;
            foodContainer.appendChild(col);
        });

        // Reattach cart events
		// Attach events to dynamically added buttons
		if (typeof window.attachCartEvents === "function") {
		    setTimeout(() => window.attachCartEvents(), 0);
		}

    }
});
