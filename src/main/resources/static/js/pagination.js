document.addEventListener("DOMContentLoaded", function () {
    const foodContainer = document.getElementById("foodContainer");
    const paginationContainer = document.createElement("div");
    paginationContainer.className = "d-flex justify-content-center mt-4 gap-2";
    paginationContainer.id = "pagination";
    foodContainer.insertAdjacentElement("afterend", paginationContainer);

    let currentPage = 0;
    let totalPages = 0;

    function fetchAndRenderPage(pageNumber = 0) {
        fetch(`/api/food/page?page=${pageNumber}&size=12`)
            .then(response => response.json())
            .then(data => {
                renderFoodCards(data.content);
                totalPages = data.totalPages;
                currentPage = data.number;
                renderPaginationButtons();
            })
            .catch(error => {
                console.error("Failed to load food items:", error);
            });
    }

    function renderFoodCards(foodList) {
        foodContainer.innerHTML = "";

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

        attachCartEvents();
    }

    function renderPaginationButtons() {
        paginationContainer.innerHTML = "";

        const prevBtn = document.createElement("button");
        prevBtn.className = "btn btn-outline-primary";
        prevBtn.textContent = "Previous";
        prevBtn.disabled = currentPage === 0;
        prevBtn.addEventListener("click", () => fetchAndRenderPage(currentPage - 1));

        const nextBtn = document.createElement("button");
        nextBtn.className = "btn btn-outline-primary";
        nextBtn.textContent = "Next";
        nextBtn.disabled = currentPage >= totalPages - 1;
        nextBtn.addEventListener("click", () => fetchAndRenderPage(currentPage + 1));

        paginationContainer.appendChild(prevBtn);
        paginationContainer.appendChild(nextBtn);
    }

    function attachCartEvents() {
        document.querySelectorAll(".increase-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                const foodId = btn.getAttribute("data-id");
                const input = document.querySelector(`input[data-id='${foodId}']`);
                let quantity = parseInt(input.value);
                quantity++;
                input.value = quantity;
            });
        });

        document.querySelectorAll(".decrease-btn").forEach(btn => {
            btn.addEventListener("click", () => {
                const foodId = btn.getAttribute("data-id");
                const input = document.querySelector(`input[data-id='${foodId}']`);
                let quantity = parseInt(input.value);
                if (quantity > 1) quantity--;
                input.value = quantity;
            });
        });

        document.querySelectorAll(".addToCartBtn").forEach(button => {
            button.addEventListener("click", function () {
                const foodId = this.getAttribute("data-id");
                const quantity = document.querySelector(`input[data-id='${foodId}']`).value;

                this.disabled = true;
                this.textContent = "Adding...";

                fetch(`/api/cart/add?foodId=${foodId}&quantity=${quantity}`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    }
                }).then(res => res.json()).then(data => {
                    const cartCountBadge = document.getElementById("cartCount");
                    const totalItems = data.reduce((sum, item) => sum + item.quantity, 0);
                    cartCountBadge.textContent = totalItems;
                    cartCountBadge.style.display = totalItems > 0 ? "inline" : "none";

                    this.textContent = "Add to Cart";
                    this.disabled = false;
                });
            });
        });
    }

    fetchAndRenderPage(); // Initial load
});
