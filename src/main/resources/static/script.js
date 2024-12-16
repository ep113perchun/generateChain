function showTab(tabId) {
    document.querySelectorAll('.tab').forEach(tab => {
        tab.classList.remove('active');
    });
    document.getElementById(tabId).classList.add('active');
}

function validateInput() {
    let isValid = true;

    const alphabet = document.getElementById('alphabet').value;
    const startSubstring = document.getElementById('startSubstring').value;
    const endSubstring = document.getElementById('endSubstring').value;
    const lengthMultiplier = document.getElementById('lengthMultiplier').value;
    const maxLength = document.getElementById('maxLength').value;

    document.querySelectorAll('.error').forEach(error => (error.textContent = ''));

    if (!/^[a-zA-Z0-9](,[a-zA-Z0-9])*$/.test(alphabet)) {
        document.getElementById('alphabetError').textContent = 'Введите алфавит в формате: a,b,c';
        isValid = false;
    }

    if (!startSubstring.trim()) {
        document.getElementById('startSubstringError').textContent = 'Начальная подцепочка обязательна';
        isValid = false;
    }

    if (!endSubstring.trim()) {
        document.getElementById('endSubstringError').textContent = 'Конечная подцепочка обязательна';
        isValid = false;
    }

    if (lengthMultiplier <= 0) {
        document.getElementById('lengthMultiplierError').textContent = 'Кратность длины должна быть больше 0';
        isValid = false;
    }

    if (maxLength <= 0) {
        document.getElementById('maxLengthError').textContent = 'Максимальная длина должна быть больше 0';
        isValid = false;
    }

    return isValid;
}

async function generateChains() {
    if (!validateInput()) {
        return;
    }

    const data = {
        alphabet: document.getElementById('alphabet').value,
        startSubstring: document.getElementById('startSubstring').value,
        endSubstring: document.getElementById('endSubstring').value,
        lengthMultiplier: parseInt(document.getElementById('lengthMultiplier').value),
        maxLength: parseInt(document.getElementById('maxLength').value)
    };

    try {
        const response = await fetch('/api/chains/generate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        const result = await response.json();

        document.getElementById('regex').textContent = result.regex || 'Нет результатов';
        document.getElementById('chains').value = result.chains.join('\n');
    } catch (error) {
        alert('Произошла ошибка при генерации. Проверьте консоль для деталей.');
        console.error(error);
    }
}

function saveChains() {
    const chains = document.getElementById('chains').value;
    if (!chains) {
        alert('Нет данных для сохранения.');
        return;
    }

    const blob = new Blob([chains], { type: 'text/plain' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = 'chains.txt';
    link.click();
}

async function generateChainsByRegex() {
    const regex = document.getElementById('regexInput').value;

    if (!regex.trim()) {
        document.getElementById('regexError').textContent = 'Регулярное выражение обязательно';
        return;
    } else {
        document.getElementById('regexError').textContent = '';
    }

    const data = { regex: regex };

    try {
        const response = await fetch('http://localhost:8080/api/chains/generatePB', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        const result = await response.json();

        document.getElementById('regex').textContent = result.regex || 'Нет результатов';
        document.getElementById('chains').value = result.chains.join('\n');
    } catch (error) {
        alert('Произошла ошибка при генерации цепочек по регулярному выражению. Проверьте консоль для деталей.');
        console.error(error);
    }
}