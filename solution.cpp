#include <iostream>
#include <vector>
#include <string>
#include <cctype>
#include <stdexcept>
using namespace std;


long long convertToDecimal(const string &val, int base) {
    long long result = 0;
    for (char c : val) {
        int digit;
        if (isdigit(c)) digit = c - '0';
        else if (isalpha(c)) digit = 10 + (tolower(c) - 'a');
        else throw invalid_argument("Invalid character in number");
        result = result * base + digit;
    }
    return result;
}

vector<long long> multiplyPoly(const vector<long long> &a, const vector<long long> &b) {
    vector<long long> res(a.size() + b.size() - 1, 0);
    for (size_t i = 0; i < a.size(); i++) {
        for (size_t j = 0; j < b.size(); j++) {
            res[i + j] += a[i] * b[j];
        }
    }
    return res;
}

int main() {
    
    int n = 10, k = 7;

    vector<pair<int, string>> inputRoots = {
        {6, "13444211440455345511"},
        {15, "aed7015a346d635"},
        {15, "6aeeb69631c227c"},
        {16, "e1b5e05623d881f"},
        {8, "316034514573652620673"},
        {3, "2122212201122002221120200210011020220200"},
        {3, "20120221122211000100210021102001201112121"},
        {6, "20220554335330240002224253"},
        {12, "45153788322a1255483"},
        {7, "1101613130313526312514143"}
    };

    vector<long long> roots;
    for (auto &p : inputRoots) {
        roots.push_back(convertToDecimal(p.second, p.first));
    }

    vector<long long> chosenRoots(roots.begin(), roots.begin() + (k - 1));

    vector<long long> poly = {1}; 
    for (long long r : chosenRoots) {
        vector<long long> factor = {1, -r}; 
        poly = multiplyPoly(poly, factor);
    }

    cout << "Polynomial coefficients (highest degree first):\n";
    for (long long coeff : poly) {
        cout << coeff << " ";
    }
    cout << endl;

    return 0;
}