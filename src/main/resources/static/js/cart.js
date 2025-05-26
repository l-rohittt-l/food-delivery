document.addEventListener("DOMContentLoaded", function () {
    const addToCartButtons = document.querySelectorAll(".addToCartBtn");
    const orderNowButtons = document.querySelectorAll(".orderNowBtn");
    const cartCountBadge = document.getElementById("cartCount");
    const toast = document.getElementById("cart-toast");
    const toastBody = document.getElementById("cart-toast-body");
    const toastBootstrap = toast ? bootstrap.Toast.getOrCreateInstance(toast) : null;

	function updateCartCount() {
	    fetch("/api/cart")
	        .then(response => {
	            if (!response.ok || !response.headers.get("content-type")?.includes("application/json")) {
	                throw new Error("Invalid response");
	            }
	            return response.json();
	        })
	        .then(data => {
	            const totalItems = data.reduce((sum, item) => sum + item.quantity, 0);
	            if (totalItems > 0) {
	                cartCountBadge.textContent = totalItems;
	                cartCountBadge.style.display = "inline";
	            } else {
	                cartCountBadge.style.display = "none";
	            }
	        })
	        .catch(error => {
	            console.error("Failed to update cart count:", error);
	        });
	}


    function handleAddToCart(event) {
        const foodId = event.target.getAttribute("data-id");
        const quantityInput = document.querySelector(`input[data-id='${foodId}']`);
        const quantity = parseInt(quantityInput?.value || "1");

        event.target.disabled = true;
        event.target.textContent = "Adding...";

        fetch(`/api/cart/add?foodId=${foodId}&quantity=${quantity}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        })
            .then(response => {
                if (!response.ok) throw new Error("Add to cart failed");
                return response.json();
            })
            .then(data => {
                const totalItems = data.reduce((sum, item) => sum + item.quantity, 0);
                cartCountBadge.textContent = totalItems;
                cartCountBadge.style.display = totalItems > 0 ? "inline" : "none";

                if (toastBody && toastBootstrap) {
                    toastBody.textContent = `${data[data.length - 1].food.name} added to cart!`;
                    toastBootstrap.show();
                }

                event.target.textContent = "Add to Cart";
                event.target.disabled = false;
            })
            .catch(error => {
                alert("Failed to add item to cart");
                event.target.textContent = "Add to Cart";
                event.target.disabled = false;
            });
    }

    function handleOrderNow(event) {
        const foodId = event.target.getAttribute("data-id");

        fetch("/api/cart")
            .then(response => response.json())
            .then(data => {
                const isAlreadyInCart = data.some(item => item.food.id == foodId);

                if (!isAlreadyInCart) {
                    return fetch(`/api/cart/add?foodId=${foodId}&quantity=1`, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json"
                        }
                    });
                }
            })
            .then(() => {
                window.location.href = "/cart";
            })
            .catch(error => {
                console.error("Order now failed:", error);
            });
    }

    function handleIncrease(event) {
        const foodId = event.target.getAttribute("data-id");
        const input = document.querySelector(`input[data-id='${foodId}']`);
        let quantity = parseInt(input.value);
        quantity++;
        input.value = quantity;
        updateQuantity(foodId, quantity);
    }

    function handleDecrease(event) {
        const foodId = event.target.getAttribute("data-id");
        const input = document.querySelector(`input[data-id='${foodId}']`);
        let quantity = parseInt(input.value);
        if (quantity > 1) {
            quantity--;
            input.value = quantity;
            updateQuantity(foodId, quantity);
        }
    }

	function updateQuantity(foodId, quantity) {
	    fetch(`/api/cart/update?foodId=${foodId}&quantity=${quantity}`, {
	        method: "PUT"
	    })
	        .then(() => updateCartCount())
	        .catch(error => console.error("Failed to update quantity:", error));
	}


    function handleRemove(event) {
        const foodId = event.target.getAttribute("data-id");

        fetch(`/api/cart/remove?foodId=${foodId}`, {
            method: "DELETE"
        })
            .then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    alert("Failed to remove item from cart");
                }
            })
            .catch(error => {
                console.error("Error removing item:", error);
            });
    }
	
	function showToast(message) {
		console.log("Toast triggered with message:", message);
	    const toastEl = document.getElementById('cart-toast');
	    const toastBody = document.getElementById('cart-toast-body');
	    toastBody.textContent = message;

	    // Remove the previous instance if exists
	    if (bootstrap.Toast.getInstance(toastEl)) {
	        bootstrap.Toast.getInstance(toastEl).dispose();
	    }

	    toast.show();
	}
	
	// 👇 Call this from search.js after rendering search results
	window.attachCartEvents = function () {
	    document.querySelectorAll(".increase-btn").forEach(button => {
	        button.removeEventListener("click", handleIncrease);
	        button.addEventListener("click", handleIncrease);
	    });

	    document.querySelectorAll(".decrease-btn").forEach(button => {
	        button.removeEventListener("click", handleDecrease);
	        button.addEventListener("click", handleDecrease);
	    });

	    document.querySelectorAll(".addToCartBtn").forEach(button => {
	        button.removeEventListener("click", handleAddToCart);
	        button.addEventListener("click", handleAddToCart);
	    });

	    document.querySelectorAll(".orderNowBtn").forEach(button => {
	        button.removeEventListener("click", handleOrderNow);
	        button.addEventListener("click", handleOrderNow);
	    });
	}



	document.querySelectorAll(".addToCartBtn").forEach(button => {
	    button.addEventListener("click", function () {
	        const foodId = this.getAttribute("data-id");
	        const quantityInput = document.querySelector(`input[data-id='${foodId}']`);
	        const quantity = parseInt(quantityInput.value) || 1;

	        fetch(`/api/cart/add/${foodId}?quantity=${quantity}`, {
	            method: "POST"
	        })
	        .then(response => {
	            if (response.ok) {
	                showToast("Item added to cart!");
	                updateCartCount(); // optional
	            } else {
	                showToast("Failed to add to cart.");
	            }
	        })
	        .catch(() => showToast("Something went wrong."));
	    });
	});

	

    addToCartButtons.forEach(button => button.addEventListener("click", handleAddToCart));
    orderNowButtons.forEach(button => button.addEventListener("click", handleOrderNow));
    document.querySelectorAll(".increase-btn").forEach(button => button.addEventListener("click", handleIncrease));
    document.querySelectorAll(".decrease-btn").forEach(button => button.addEventListener("click", handleDecrease));
    document.querySelectorAll(".remove-btn").forEach(button => button.addEventListener("click", handleRemove));

    updateCartCount();
});