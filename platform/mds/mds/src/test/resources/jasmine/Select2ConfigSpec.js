(function () {
    'use strict';

    describe('Testing select2 configuration', function () {
        var entity, scope, config, httpBackend;

        beforeEach(module('mds'));

        beforeEach(inject(function(_$httpBackend_, $rootScope, $controller) {
            httpBackend = _$httpBackend_;
            scope = $rootScope.$new();

            scope.msg = function (arg) {
                return arg;
            };

            httpBackend.expectGET('../mds/available/types').respond([]);

            $controller('SchemaEditorCtrl', { $scope: scope });
            config = scope.SELECT_ENTITY_CONFIG;

            expect(config).not.toEqual(undefined);
        }));

        beforeEach(function () {
            entity = {
                name: 'test-name',
                module: 'test-module',
                namespace: 'test-namespace'
            };
        });

        it('Should correctly format the selected entity', function () {
            expect(config.formatSelection(entity))
                .toEqual('test-name (mds.module: test-module  mds.namespace: test-namespace)');

            delete entity.module;
            expect(config.formatSelection(entity))
                .toEqual('test-name (mds.namespace: test-namespace)');

            delete entity.namespace;
            entity.module = 'test-module';
            expect(config.formatSelection(entity))
                .toEqual('test-name (mds.module: test-module)');

            delete entity.module;
            delete entity.namespace;
            expect(config.formatSelection(entity)).toEqual('test-name');
        });

        it('Should return error message if label for an selected entity cannot be created',
            function () {
            expect(config.formatSelection({})).toEqual('mds.error');
            expect(config.formatSelection(null)).toEqual('mds.error');
            expect(config.formatSelection(undefined)).toEqual('mds.error');
        });

        it('Should correctly format an entity on list', function () {
            var strong = angular.element('<strong>').text(entity.name),
                name = angular.element('<div>').append(strong),
                module = angular.element('<span>')
                        .text(' {0}: {1}'.format(scope.msg('mds.module'), entity.module)),
                namespace = angular.element('<span>')
                        .text(' {0}: {1}'.format(scope.msg('mds.namespace'), entity.namespace)),
                info = angular.element('<div>').append(module).append(namespace),
                parent = angular.element('<div>').append(name).append(info);


            expect(config.formatResult(entity).wrapAll('<div></div>').parent().html())
                .toEqual(parent.wrapAll('<div></div>').parent().html());

            delete entity.module;
            info = angular.element('<div>').append(namespace);
            parent = angular.element('<div>').append(name).append(info);

            expect(config.formatResult(entity).wrapAll('<div></div>').parent().html())
                .toEqual(parent.wrapAll('<div></div>').parent().html());

            delete entity.namespace;
            entity.module = 'test-module';
            info = angular.element('<div>').append(module);
            parent = angular.element('<div>').append(name).append(info);

            expect(config.formatResult(entity).wrapAll('<div></div>').parent().html())
                .toEqual(parent.wrapAll('<div></div>').parent().html());

            delete entity.module;
            delete entity.namespace;
            parent = angular.element('<div>').append(name);

            expect(config.formatResult(entity).wrapAll('<div></div>').parent().html())
                .toEqual(parent.wrapAll('<div></div>').parent().html());
        });

        it('Should return error message if label for an entity on list cannot be created',
            function () {
            expect(config.formatResult({})).toEqual('mds.error');
            expect(config.formatResult(null)).toEqual('mds.error');
            expect(config.formatResult(undefined)).toEqual('mds.error');
        });

    });

}());
