$(document).ready(function (){
    $('.table .view').on('click', function(event){

        event.preventDefault();
        var href = $(this).attr('href');

        $.get(href, function(user, status){
            $('#modal-id').text(user.id);
            $('#modal-firstname').text(user.firstName);
            $('#modal-lastname').text(user.lastName);
            $('#modal-username').text(user.username);
            $('#modal-city').text(user.city);
            $('#modal-role').text(user.roles.map(role => role.name).join(", "));
        });
        $('#exampleModal1').modal('show');
    })
});

$(document).ready(function (){
    $('.table .edit').on('click', function(event){

        event.preventDefault();
        // var href = $(this).attr('href');
        //
        // $.get(href, function(user, status){
        //     $('#id').val(user.id);
        //     $('#firstname').val(user.firstName);
        //     $('#lastname').val(user.lastName);
        //     $('#username').val(user.username);
        //     $('#city').val(user.city);
        //     $('#role').val(user.roles.map(role => role.name).join(", "));
        // });
        $('#exampleModal2').modal('show');
    })




});

