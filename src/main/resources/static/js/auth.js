
/****************************************************************
 *   TAB SWITCHING
 ****************************************************************/
const tabBtns = document.querySelectorAll('.tab-btn');
const forms = document.querySelectorAll('.auth-form');

tabBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        const tabName = btn.dataset.tab;
        // Убираем активные классы
        tabBtns.forEach(b => b.classList.remove('active'));
        forms.forEach(f => f.classList.remove('active'));

        // Добавляем активный класс
        btn.classList.add('active');
        document.getElementById(`${tabName}Form`).classList.add('active');
    });
});


/****************************************************************
 *   TOGGLE PASSWORD
 ****************************************************************/
const togglePasswordBtns = document.querySelectorAll('.toggle-password');

togglePasswordBtns.forEach(btn => {
    btn.addEventListener('click', () => {
        const input = btn.previousElementSibling;
        const icon = btn.querySelector('i');

        if (input.type === 'password') {
            input.type = 'text';
            icon.classList.remove('fa-eye');
            icon.classList.add('fa-eye-slash');
        } else {
            input.type = 'password';
            icon.classList.remove('fa-eye-slash');
            icon.classList.add('fa-eye');
        }
    });
});


/****************************************************************
 *   FORMS & PATTERNS
 ****************************************************************/
const loginForm = document.getElementById('loginForm');
const loginEmailInput = document.getElementById('loginEmail');
const loginPasswordInput = document.getElementById('loginPassword');
const loginSubmitBtn = loginForm.querySelector('.auth-button');

const registerForm = document.getElementById('registerForm');
const nameInput = document.getElementById('registerName');
const emailInput = document.getElementById('registerEmail');
const passwordInput = document.getElementById('registerPassword');
const confirmPasswordInput = document.getElementById('confirmPassword');
const termsCheckbox = document.getElementById('termsAccept');
const registerSubmitBtn = registerForm.querySelector('.auth-button');

// Шаблоны для валидации
// Login: 8+ символов пароль, Email - минимум @
const loginPatterns = {
    email: /^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/,
    password: /^.{8,}$/
};

// Регистрация: Name ≥2 буквы, Email, Password ≥8 (1 буква, 1 цифра)
const registerPatterns = {
    name: /^[A-Za-z\s]{2,}$/,
    email: /^[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}$/,
    password: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/
};

// Сообщения
const errorMessages = {
    required: 'This field is required',
    // Login
    email: 'Please enter a valid email address',
    password: 'Password must be at least 8 characters',
    // Register
    name: 'Please enter a valid name (minimum 2 characters)',
    registerPassword: 'Password must be at least 8 characters with 1 letter and 1 number',
    confirmPassword: 'Passwords do not match',
    terms: 'You must accept the terms and conditions'
};


/****************************************************************
 *   LOGIN VALIDATION
 ****************************************************************/
function validateLoginInput(input, pattern) {
    const formGroup = input.closest('.form-group');
    if (!formGroup) return false;

    const errorElement = formGroup.querySelector('.error-message');
    if (!errorElement) return false;

    if (input.required && !input.value.trim()) {
        errorElement.textContent = errorMessages.required;
        input.classList.add('invalid');
        return false;
    }
    if (pattern && !pattern.test(input.value)) {
        // Либо email, либо password
        errorElement.textContent = errorMessages[input.name] || 'Invalid input';
        input.classList.add('invalid');
        return false;
    }
    // OK
    errorElement.textContent = '';
    input.classList.remove('invalid');
    return true;
}

// Проверяем и включаем/выключаем кнопку
function updateLoginButton() {
    const invalidFields = loginForm.querySelectorAll('.invalid');
    loginSubmitBtn.disabled = invalidFields.length > 0;
}

// Повесим события
[loginEmailInput, loginPasswordInput].forEach(inp => {
    inp.addEventListener('input', () => {
        if (inp.id === 'loginEmail') {
            validateLoginInput(inp, loginPatterns.email);
        } else {
            validateLoginInput(inp, loginPatterns.password);
        }
        updateLoginButton();
    });
});

// При потере фокуса, ещё раз проверим
[loginEmailInput, loginPasswordInput].forEach(inp => {
    inp.addEventListener('blur', () => {
        if (inp.id === 'loginEmail') {
            validateLoginInput(inp, loginPatterns.email);
        } else {
            validateLoginInput(inp, loginPatterns.password);
        }
        updateLoginButton();
    });
});

// Submit логина
loginForm.addEventListener('submit', (e) => {
    e.preventDefault(); // мы пока блокируем сабмит
    const isEmailValid = validateLoginInput(loginEmailInput, loginPatterns.email);
    const isPassValid = validateLoginInput(loginPasswordInput, loginPatterns.password);
    updateLoginButton();

    if (isEmailValid && isPassValid && !loginSubmitBtn.disabled) {
        // Если всё ок — отправляем форму на сервер по реальному action
        console.log("Trying to submit form /login");
        loginForm.submit();
    }
});


/****************************************************************
 *   REGISTER VALIDATION
 ****************************************************************/
// Проверка одиночного поля (name, email, password)
function validateRegisterField(input) {
    const formGroup = input.closest('.form-group');
    if (!formGroup) return false;

    const errorElement = formGroup.querySelector('.error-message');
    if (!errorElement) return false;

    if (input.required && !input.value.trim()) {
        errorElement.textContent = errorMessages.required;
        input.classList.add('invalid');
        return false;
    }

    // Определим, какое именно поле
    if (input.id === 'registerName') {
        if (!registerPatterns.name.test(input.value)) {
            errorElement.textContent = errorMessages.name;
            input.classList.add('invalid');
            return false;
        }
    }
    else if (input.id === 'registerEmail') {
        if (!registerPatterns.email.test(input.value)) {
            errorElement.textContent = errorMessages.email;
            input.classList.add('invalid');
            return false;
        }
    }
    else if (input.id === 'registerPassword') {
        if (!registerPatterns.password.test(input.value)) {
            errorElement.textContent = errorMessages.registerPassword;
            input.classList.add('invalid');
            return false;
        }
    }

    // Если всё ОК
    errorElement.textContent = '';
    input.classList.remove('invalid');
    return true;
}

// Проверка совпадения пароля
function validateConfirmPassword(passwordInput, confirmPasswordInput) {
    const formGroup = confirmPasswordInput.closest('.form-group');
    if (!formGroup) return false;

    const errorElement = formGroup.querySelector('.error-message');
    if (!errorElement) return false;

    if (!confirmPasswordInput.value.trim()) {
        errorElement.textContent = errorMessages.required;
        confirmPasswordInput.classList.add('invalid');
        return false;
    }

    if (passwordInput.value !== confirmPasswordInput.value) {
        errorElement.textContent = errorMessages.confirmPassword;
        confirmPasswordInput.classList.add('invalid');
        return false;
    }

    // OK
    errorElement.textContent = '';
    confirmPasswordInput.classList.remove('invalid');
    return true;
}

// Проверка чекбокса (terms)
function validateTerms(checkbox) {
    const formGroup = checkbox.closest('.form-group');
    if (!formGroup) return false;

    const errorElement = formGroup.querySelector('.error-message');
    if (!errorElement) return false;

    if (!checkbox.checked) {
        errorElement.textContent = errorMessages.terms;
        checkbox.classList.add('invalid');
        return false;
    }

    // OK
    errorElement.textContent = '';
    checkbox.classList.remove('invalid');
    return true;
}

// После каждой проверки обновляем кнопку
function updateRegisterButton() {
    const invalidFields = registerForm.querySelectorAll('.invalid');
    registerSubmitBtn.disabled = invalidFields.length > 0;
}

// Слушаем ввод на основных полях (имя, email, пароль, подтверждение)
[nameInput, emailInput, passwordInput, confirmPasswordInput].forEach(el => {
    el.addEventListener('input', () => {
        if (el.id === 'registerName' || el.id === 'registerEmail' || el.id === 'registerPassword') {
            validateRegisterField(el);
        }
        if (el.id === 'confirmPassword' || el.id === 'registerPassword') {
            validateConfirmPassword(passwordInput, confirmPasswordInput);
        }
        updateRegisterButton();
    });

    // На потерю фокуса тоже
    el.addEventListener('blur', () => {
        if (el.id === 'registerName' || el.id === 'registerEmail' || el.id === 'registerPassword') {
            validateRegisterField(el);
        }
        if (el.id === 'confirmPassword' || el.id === 'registerPassword') {
            validateConfirmPassword(passwordInput, confirmPasswordInput);
        }
        updateRegisterButton();
    });
});

// Чекбокс
termsCheckbox.addEventListener('change', () => {
    validateTerms(termsCheckbox);
    updateRegisterButton();
});

// Submit регистрации
registerForm.addEventListener('submit', (e) => {
    e.preventDefault(); // блокируем сабмит
    const isNameValid = validateRegisterField(nameInput);
    const isEmailValid = validateRegisterField(emailInput);
    const isPassValid = validateRegisterField(passwordInput);
    const isConfirmOk = validateConfirmPassword(passwordInput, confirmPasswordInput);
    const isTermsOk = validateTerms(termsCheckbox);

    updateRegisterButton();

    if (isNameValid && isEmailValid && isPassValid && isConfirmOk && isTermsOk && !registerSubmitBtn.disabled) {
        console.log("Trying to submit form /registration");
        // Если валидация пройдена — отправляем форму
        registerForm.submit();
    }
});

// Изначально проверяем (кнопки заблокированы)
updateLoginButton();
updateRegisterButton();