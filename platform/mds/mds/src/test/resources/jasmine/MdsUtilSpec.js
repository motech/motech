(function () {
    'use strict';

    describe('Testing Mds utility functions', function () {

        it('Should convert the given string to camelCase', function () {
            expect(camelCase(undefined)).toEqual('');
            expect(camelCase(null)).toEqual('');
            expect(camelCase('')).toEqual('');
            expect(camelCase('Test')).toEqual('test');
            expect(camelCase('test')).toEqual('test');
            expect(camelCase('two words')).toEqual('twoWords');
            expect(camelCase('A very Long string with Upper and lower Letters'))
                .toEqual('aVeryLongStringWithUpperAndLowerLetters');
            expect(camelCase('A Very Long String With Upper Letters'))
                .toEqual('aVeryLongStringWithUpperLetters');
        });

        it('Should find appropriate elements from array', function () {
            var data, predicates, unique, label, expected;

            expected = [];
            expect(find(data, predicates, unique, label)).toEqual(expected);

            unique = true;
            expected = {};
            expect(find(data, predicates, unique, label)).toEqual(expected);

            data = [{name: '1', order: '1'},{name: '2', order: '2'},{name: '1', order: '3'}];
            predicates = [{field: 'name', value: '1'}];
            unique = false;
            expected = [{name: '1', order: '1'},{name: '1', order: '3'}];
            expect(find(data, predicates, unique, label)).toEqual(expected);

            data = [{name: '1', order: '1'},{name: '2', order: '2'},{name: '1', order: '3'}];
            predicates = [{field: 'name', value: '1'}];
            unique = true;
            expected = {name: '1', order: '1'};
            expect(find(data, predicates, unique, label)).toEqual(expected);

            data = [
                {name: {first: 'first1', last: 'last1'}, order: '2'},
                {name: {first: 'first2', last: 'last2'}, order: '2'}
            ];
            predicates = [{field: 'name.first', value: 'first2'}];
            unique = true;
            expected = {name: {first: 'first2', last: 'last2'}, order: '2'};
            expect(find(data, predicates, unique, label)).toEqual(expected);

            data = [{name: '1', order: '1'},{name: '2', order: '2'},{name: '1', order: '3'}];
            predicates = [{field: 'name', value: '3'}];
            unique = true;
            expected = {name: '2', order: '2'};
            label = function (arg) { return arg === '2' ? '3' : arg; };
            expect(find(data, predicates, unique, label)).toEqual(expected);
        });

        it('Should check if given number has appropriate precision and scale', function () {
            expect(validateDecimal(1234567.89)).toEqual(true);
            expect(validateDecimal(123.45)).toEqual(true);
            expect(validateDecimal(1234, 4)).toEqual(true);
            expect(validateDecimal(1.2345, 5, 4)).toEqual(true);
            expect(validateDecimal(0.12345, 6, 5)).toEqual(true);

            expect(validateDecimal(null)).toEqual(false);
            expect(validateDecimal(undefined)).toEqual(false);
            expect(validateDecimal(1234, 2, 0)).toEqual(false);
            expect(validateDecimal(12345.67, 7, 1)).toEqual(false);
        });

    });

}());
