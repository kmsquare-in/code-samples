// See https://en.wikipedia.org/wiki/Sieve_of_Eratosthenes

function incrementBy1(n) {
    if (typeof javaCxtObj != "undefined") {
        return javaCxtObj.incrementBy1(n);
    } else {
        return n + 1;
    }
}

function NumberHolder(n) {
    return {
        "value": n,
        "marked": false
    };
}

function filterNumbersByMultiplesOfPButP (numbers, p) {
    return numbers.filter (function (number) {
        return number.value != p && number.value % p == 0;
    });
}

function findSmallestNumberAfterPThatIsNotMarked(numbers, p) {
    let i = incrementBy1(p);
    let selected = -1;
    do {
        if (numbers[i].marked == false) {
            return numbers[i].value;
        }
        i = incrementBy1(i);
    } while (selected == -1 && i < numbers.length - 1);
    return selected;
}

function find_last_prime_within_n(n) {
    if (n < 2) {
        throw Error ("n cannot be less than 2");
    }
    let numbers = [];
    for (let i = 0; i <= n; i++) {
        // Starting from 0 to use the array index as number (only 0 and 1 are extra storage in memory).
        // Used by findSmallestNumberAfterPThatIsNotMarked
        numbers.push(new NumberHolder(i));
    }
    let p = 2;
    do {
        filterNumbersByMultiplesOfPButP (numbers, p).forEach(number => {
            number.marked = true;
        });
        p = findSmallestNumberAfterPThatIsNotMarked(numbers, p);
    } while (p != -1);
    let i = numbers.length - 1;
    do {
        if (numbers[i].marked == false) {
            return numbers[i].value;
        }
        --i;
    } while (i > 0);
    return 0;
}

let output = find_last_prime_within_n (input);
if (output != expected) {
    throw Error (`Output [${output}] is not same as expected [${expected}]`);
}
output;
